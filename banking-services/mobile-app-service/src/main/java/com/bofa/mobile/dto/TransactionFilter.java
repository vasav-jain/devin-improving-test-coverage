package com.bofa.mobile.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class TransactionFilter {
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    public Optional<LocalDate> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Optional<BigDecimal> getMinAmount() {
        return Optional.ofNullable(minAmount);
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public Optional<BigDecimal> getMaxAmount() {
        return Optional.ofNullable(maxAmount);
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
}
