package com.bofa.mobile.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class MobileTransaction {
    private final String transactionId;
    private final String accountId;
    private final BigDecimal amount;
    private final String category;
    private final LocalDate date;

    public MobileTransaction(String accountId, BigDecimal amount, String category, LocalDate date) {
        this.transactionId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
