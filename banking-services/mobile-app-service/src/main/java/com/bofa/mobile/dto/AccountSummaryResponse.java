package com.bofa.mobile.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AccountSummaryResponse {
    private final String userId;
    private final BigDecimal totalBalance;
    private final BigDecimal averageDebit;
    private final BigDecimal averageCredit;
    private final Map<String, BigDecimal> categoryTotals;

    public AccountSummaryResponse(String userId,
                                  BigDecimal totalBalance,
                                  BigDecimal averageDebit,
                                  BigDecimal averageCredit,
                                  Map<String, BigDecimal> categoryTotals) {
        this.userId = userId;
        this.totalBalance = totalBalance;
        this.averageDebit = averageDebit;
        this.averageCredit = averageCredit;
        this.categoryTotals = categoryTotals;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public BigDecimal getAverageDebit() {
        return averageDebit;
    }

    public BigDecimal getAverageCredit() {
        return averageCredit;
    }

    public Map<String, BigDecimal> getCategoryTotals() {
        return categoryTotals;
    }
}
