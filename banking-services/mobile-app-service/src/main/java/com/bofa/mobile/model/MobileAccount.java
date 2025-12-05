package com.bofa.mobile.model;

import java.math.BigDecimal;
import java.util.UUID;

public class MobileAccount {
    private final String accountId;
    private final String userId;
    private String nickname;
    private BigDecimal balance;

    public MobileAccount(String userId, String nickname, BigDecimal balance) {
        this.accountId = UUID.randomUUID().toString();
        this.userId = userId;
        this.nickname = nickname;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
