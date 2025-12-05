package com.bofa.payments.controller;

import com.bofa.payments.dto.InterestCalculationRequest;
import com.bofa.payments.dto.InterestCalculationResponse;
import com.bofa.payments.dto.MortgageEstimateRequest;
import com.bofa.payments.dto.MortgageEstimateResponse;
import com.bofa.payments.dto.PaymentExecutionRequest;
import com.bofa.payments.dto.PaymentResponse;
import com.bofa.payments.dto.PaymentScheduleRequest;
import com.bofa.payments.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse schedulePayment(@Valid @RequestBody PaymentScheduleRequest request) {
        return paymentService.schedulePayment(request);
    }

    @PostMapping("/payments/execute/{paymentId}")
    public PaymentResponse executePayment(@PathVariable String paymentId,
                                           @Valid @RequestBody PaymentExecutionRequest request) {
        return paymentService.executePayment(paymentId, request);
    }

    @GetMapping("/payments/history/{accountId}")
    public List<PaymentResponse> getPaymentHistory(@PathVariable String accountId) {
        return paymentService.getPaymentHistory(accountId);
    }

    @PostMapping("/interest/calculate")
    public InterestCalculationResponse calculateInterest(@Valid @RequestBody InterestCalculationRequest request) {
        return paymentService.calculateInterest(request);
    }

    @PostMapping("/mortgage/estimate")
    public MortgageEstimateResponse estimateMortgage(@Valid @RequestBody MortgageEstimateRequest request) {
        return paymentService.estimateMortgage(request);
    }
}
