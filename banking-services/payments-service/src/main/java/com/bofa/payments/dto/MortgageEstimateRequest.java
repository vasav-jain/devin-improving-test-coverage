package com.bofa.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MortgageEstimateRequest {

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal annualRate;

    @Min(1)
    private int termMonths;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal propertyValue;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal downPayment;

    private BigDecimal optionalMonthlyPrepayment = BigDecimal.ZERO;

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(BigDecimal propertyValue) {
        this.propertyValue = propertyValue;
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }

    public BigDecimal getOptionalMonthlyPrepayment() {
        return optionalMonthlyPrepayment;
    }

    public void setOptionalMonthlyPrepayment(BigDecimal optionalMonthlyPrepayment) {
        this.optionalMonthlyPrepayment = optionalMonthlyPrepayment;
    }
}
