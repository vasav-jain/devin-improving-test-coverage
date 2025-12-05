package com.bofa.payments.dto;

import java.math.BigDecimal;

public class AmortizationInstallment {
    private int month;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal remainingBalance;
    private boolean prepaymentPenaltyApplied;

    public AmortizationInstallment(int month,
                                   BigDecimal principalComponent,
                                   BigDecimal interestComponent,
                                   BigDecimal remainingBalance,
                                   boolean prepaymentPenaltyApplied) {
        this.month = month;
        this.principalComponent = principalComponent;
        this.interestComponent = interestComponent;
        this.remainingBalance = remainingBalance;
        this.prepaymentPenaltyApplied = prepaymentPenaltyApplied;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getPrincipalComponent() {
        return principalComponent;
    }

    public BigDecimal getInterestComponent() {
        return interestComponent;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public boolean isPrepaymentPenaltyApplied() {
        return prepaymentPenaltyApplied;
    }
}
