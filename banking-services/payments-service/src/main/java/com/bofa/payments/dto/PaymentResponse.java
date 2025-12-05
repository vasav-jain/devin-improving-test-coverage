package com.bofa.payments.dto;

import com.bofa.payments.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponse {
    private String paymentId;
    private String accountId;
    private BigDecimal principal;
    private BigDecimal interest;
    private LocalDate scheduledDate;
    private LocalDate executedDate;
    private PaymentStatus status;

    public PaymentResponse(String paymentId,
                           String accountId,
                           BigDecimal principal,
                           BigDecimal interest,
                           LocalDate scheduledDate,
                           LocalDate executedDate,
                           PaymentStatus status) {
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.principal = principal;
        this.interest = interest;
        this.scheduledDate = scheduledDate;
        this.executedDate = executedDate;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalDate getExecutedDate() {
        return executedDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
