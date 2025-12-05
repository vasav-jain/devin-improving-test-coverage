package com.bofa.payments.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private String paymentId;
    private String accountId;
    private BigDecimal principal;
    private BigDecimal interest;
    private LocalDate scheduledDate;
    private LocalDate executedDate;
    private PaymentStatus status;

    public Payment(String paymentId,
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

    public Payment() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(LocalDate executedDate) {
        this.executedDate = executedDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }
}
