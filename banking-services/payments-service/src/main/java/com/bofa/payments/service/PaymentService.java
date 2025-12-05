package com.bofa.payments.service;

import com.bofa.payments.dto.InterestCalculationRequest;
import com.bofa.payments.dto.InterestCalculationResponse;
import com.bofa.payments.dto.MortgageEstimateRequest;
import com.bofa.payments.dto.MortgageEstimateResponse;
import com.bofa.payments.dto.PaymentExecutionRequest;
import com.bofa.payments.dto.PaymentResponse;
import com.bofa.payments.dto.PaymentScheduleRequest;
import com.bofa.payments.exception.PaymentValidationException;
import com.bofa.payments.model.Payment;
import com.bofa.payments.model.PaymentStatus;
import com.bofa.payments.repository.PaymentRepository;
import com.bofa.payments.util.PaymentMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InterestCalculator interestCalculator;
    private final MortgageCalculator mortgageCalculator;

    public PaymentService(PaymentRepository paymentRepository,
                          InterestCalculator interestCalculator,
                          MortgageCalculator mortgageCalculator) {
        this.paymentRepository = paymentRepository;
        this.interestCalculator = interestCalculator;
        this.mortgageCalculator = mortgageCalculator;
    }

    public PaymentResponse schedulePayment(PaymentScheduleRequest request) {
        validateScheduleRequest(request);
        Payment payment = paymentRepository.createScheduled(
                request.getAccountId(),
                request.getPrincipal(),
                request.getInterest(),
                request.getScheduledDate());
        return PaymentMapper.toResponse(payment);
    }

    public PaymentResponse executePayment(String paymentId, PaymentExecutionRequest request) {
        Payment payment = paymentRepository.find(paymentId);
        if (payment.getStatus() != PaymentStatus.SCHEDULED) {
            throw new PaymentValidationException("Only scheduled payments can be executed");
        }
        LocalDate executionDate = request.getExecutionDate();
        if (executionDate.isBefore(payment.getScheduledDate().minusDays(3))) {
            throw new PaymentValidationException("Execution date cannot be earlier than 3 days before scheduled date");
        }
        if (executionDate.isAfter(payment.getScheduledDate().plusDays(15))) {
            throw new PaymentValidationException("Execution date cannot be more than 15 days after scheduled date");
        }

        if (request.isPartialPayment()) {
            BigDecimal reducedPrincipal = payment.getPrincipal().multiply(BigDecimal.valueOf(0.5));
            payment.setPrincipal(reducedPrincipal);
            payment.setStatus(PaymentStatus.PARTIAL);
        }

        if (request.getFailureReason() != null) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setExecutedDate(executionDate);
        } else {
            payment.setStatus(PaymentStatus.EXECUTED);
            payment.setExecutedDate(executionDate);
            if (!request.isWaiveInterest()) {
                long days = ChronoUnit.DAYS.between(payment.getScheduledDate(), executionDate);
                BigDecimal accrued = interestCalculator.calculateDailyCompound(
                        payment.getPrincipal(),
                        payment.getInterest(),
                        (int) Math.max(days, 0));
                payment.setInterest(payment.getInterest().add(accrued));
            }
        }

        paymentRepository.update(payment);
        return PaymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getPaymentHistory(String accountId) {
        return paymentRepository.findByAccount(accountId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public InterestCalculationResponse calculateInterest(InterestCalculationRequest request) {
        BigDecimal accrued = interestCalculator.calculateDailyCompound(
                request.getPrincipal(), request.getAnnualRate(), request.getDays());
        BigDecimal total = request.getPrincipal().add(accrued);
        return new InterestCalculationResponse(accrued, total);
    }

    public MortgageEstimateResponse estimateMortgage(MortgageEstimateRequest request) {
        return mortgageCalculator.estimate(request);
    }

    private void validateScheduleRequest(PaymentScheduleRequest request) {
        if (request.getPrincipal() == null || request.getPrincipal().compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new PaymentValidationException("Principal must be at least 100");
        }
        if (request.getScheduledDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new PaymentValidationException("Scheduled date must be at least 1 day in the future");
        }
    }
}
