package com.bofa.compliance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionProfile {

    @NotBlank
    private String userId;

    @NotBlank
    private String counterpartyCountry;

    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @DecimalMin("0.00")
    private BigDecimal averageDailyAmount;

    @DecimalMin("0.00")
    private BigDecimal weeklyVolume;

    private int sanctionsMatches;

    @NotNull
    private RiskChannel channel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCounterpartyCountry() {
        return counterpartyCountry;
    }

    public void setCounterpartyCountry(String counterpartyCountry) {
        this.counterpartyCountry = counterpartyCountry;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAverageDailyAmount() {
        return averageDailyAmount;
    }

    public void setAverageDailyAmount(BigDecimal averageDailyAmount) {
        this.averageDailyAmount = averageDailyAmount;
    }

    public BigDecimal getWeeklyVolume() {
        return weeklyVolume;
    }

    public void setWeeklyVolume(BigDecimal weeklyVolume) {
        this.weeklyVolume = weeklyVolume;
    }

    public int getSanctionsMatches() {
        return sanctionsMatches;
    }

    public void setSanctionsMatches(int sanctionsMatches) {
        this.sanctionsMatches = sanctionsMatches;
    }

    public RiskChannel getChannel() {
        return channel;
    }

    public void setChannel(RiskChannel channel) {
        this.channel = channel;
    }
}
