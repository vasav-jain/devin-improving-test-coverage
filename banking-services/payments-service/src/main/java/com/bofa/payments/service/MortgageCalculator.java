package com.bofa.payments.service;

import com.bofa.payments.dto.AmortizationInstallment;
import com.bofa.payments.dto.MortgageEstimateRequest;
import com.bofa.payments.dto.MortgageEstimateResponse;
import com.bofa.payments.exception.PaymentValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class MortgageCalculator {

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);
    private static final BigDecimal PMI_THRESHOLD = BigDecimal.valueOf(0.80);
    private static final BigDecimal PMI_RATE = BigDecimal.valueOf(0.005); // 0.5% annually

    public MortgageEstimateResponse estimate(MortgageEstimateRequest request) {
        validate(request);

        BigDecimal baseLoan = request.getLoanAmount();
        BigDecimal monthlyRate = request.getAnnualRate().divide(BigDecimal.valueOf(12 * 100), MC);
        int term = request.getTermMonths();

        if (term <= 0) {
            throw new PaymentValidationException("Term must be positive");
        }

        BigDecimal numerator = monthlyRate.multiply(baseLoan, MC);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.add(monthlyRate).pow(-term, MC), MC);
        BigDecimal monthlyPayment = numerator.divide(denominator, MC);
        monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_EVEN);

        boolean pmiRequired = isPmiRequired(request);
        if (pmiRequired) {
            BigDecimal monthlyPmi = baseLoan.multiply(PMI_RATE, MC).divide(BigDecimal.valueOf(12), MC);
            monthlyPayment = monthlyPayment.add(monthlyPmi.setScale(2, RoundingMode.HALF_EVEN));
        }

        List<AmortizationInstallment> schedule = buildSchedule(baseLoan, monthlyRate, monthlyPayment,
                term, request.getOptionalMonthlyPrepayment());
        int payoffMonth = schedule.isEmpty() ? 0 : schedule.get(schedule.size() - 1).getMonth();

        return new MortgageEstimateResponse(monthlyPayment, pmiRequired, payoffMonth, schedule);
    }

    private boolean isPmiRequired(MortgageEstimateRequest request) {
        BigDecimal loanToValue = request.getLoanAmount().divide(request.getPropertyValue(), MC);
        return loanToValue.compareTo(PMI_THRESHOLD) > 0;
    }

    private List<AmortizationInstallment> buildSchedule(BigDecimal principal,
                                                         BigDecimal monthlyRate,
                                                         BigDecimal monthlyPayment,
                                                         int term,
                                                         BigDecimal prepayment) {
        List<AmortizationInstallment> schedule = new ArrayList<>();
        BigDecimal balance = principal;
        BigDecimal penaltyRate = BigDecimal.valueOf(0.02);

        for (int month = 1; month <= term && balance.compareTo(BigDecimal.ZERO) > 0; month++) {
            BigDecimal interestComponent = balance.multiply(monthlyRate, MC);
            BigDecimal principalComponent = monthlyPayment.subtract(interestComponent, MC);
            BigDecimal extraPayment = prepayment.min(balance);
            boolean applyPenalty = prepayment.compareTo(BigDecimal.ZERO) > 0 && month <= 24;

            BigDecimal penalty = applyPenalty ? extraPayment.multiply(penaltyRate, MC) : BigDecimal.ZERO;
            BigDecimal totalPrincipalReduction = principalComponent.add(extraPayment, MC);

            if (totalPrincipalReduction.compareTo(balance) > 0) {
                totalPrincipalReduction = balance;
            }

            balance = balance.subtract(totalPrincipalReduction, MC);

            schedule.add(new AmortizationInstallment(
                    month,
                    principalComponent.setScale(2, RoundingMode.HALF_EVEN),
                    interestComponent.setScale(2, RoundingMode.HALF_EVEN),
                    balance.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_EVEN),
                    penalty.compareTo(BigDecimal.ZERO) > 0
            ));

            if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }
        return schedule;
    }

    private void validate(MortgageEstimateRequest request) {
        if (request.getLoanAmount() == null || request.getLoanAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new PaymentValidationException("Loan amount must be at least 10,000");
        }
        if (request.getAnnualRate() == null || request.getAnnualRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Annual rate must be positive");
        }
        if (request.getTermMonths() < 60 || request.getTermMonths() > 480) {
            throw new PaymentValidationException("Term must be between 60 and 480 months");
        }
        if (request.getPropertyValue() == null || request.getPropertyValue().compareTo(request.getLoanAmount()) < 0) {
            throw new PaymentValidationException("Property value must exceed loan amount");
        }
    }
}
