package com.bofa.payments.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentScheduleRequest {

    @NotBlank
    private String accountId;

    @NotNull
    @DecimalMin(value = "1.00", message = "Principal must be positive")
    private BigDecimal principal;

    @NotNull
    @DecimalMin(value = "0.00", message = "Interest cannot be negative")
    private BigDecimal interest;

    @NotNull
    @Future
    private LocalDate scheduledDate;

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
}
