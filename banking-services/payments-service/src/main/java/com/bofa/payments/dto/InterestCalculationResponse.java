package com.bofa.payments.dto;

import java.math.BigDecimal;

public class InterestCalculationResponse {
    private BigDecimal accruedInterest;
    private BigDecimal totalAmount;

    public InterestCalculationResponse(BigDecimal accruedInterest, BigDecimal totalAmount) {
        this.accruedInterest = accruedInterest;
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAccruedInterest() {
        return accruedInterest;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
