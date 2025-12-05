package com.bofa.payments.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PaymentExecutionRequest {

    @NotNull
    private LocalDate executionDate;

    private boolean partialPayment;

    private boolean waiveInterest;

    private String failureReason;

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public boolean isPartialPayment() {
        return partialPayment;
    }

    public void setPartialPayment(boolean partialPayment) {
        this.partialPayment = partialPayment;
    }

    public boolean isWaiveInterest() {
        return waiveInterest;
    }

    public void setWaiveInterest(boolean waiveInterest) {
        this.waiveInterest = waiveInterest;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
