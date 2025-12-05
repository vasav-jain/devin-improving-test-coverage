package com.bofa.payments.dto;

import java.math.BigDecimal;
import java.util.List;

public class MortgageEstimateResponse {
    private BigDecimal monthlyPayment;
    private boolean pmiRequired;
    private int estimatedMonthsToPayoff;
    private List<AmortizationInstallment> schedule;

    public MortgageEstimateResponse(BigDecimal monthlyPayment,
                                    boolean pmiRequired,
                                    int estimatedMonthsToPayoff,
                                    List<AmortizationInstallment> schedule) {
        this.monthlyPayment = monthlyPayment;
        this.pmiRequired = pmiRequired;
        this.estimatedMonthsToPayoff = estimatedMonthsToPayoff;
        this.schedule = schedule;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public boolean isPmiRequired() {
        return pmiRequired;
    }

    public int getEstimatedMonthsToPayoff() {
        return estimatedMonthsToPayoff;
    }

    public List<AmortizationInstallment> getSchedule() {
        return schedule;
    }
}
