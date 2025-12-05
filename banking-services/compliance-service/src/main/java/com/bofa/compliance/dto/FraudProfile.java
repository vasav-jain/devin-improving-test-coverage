package com.bofa.compliance.dto;

public class FraudProfile {
    private String userId;
    private int deviceRiskScore;
    private int transactionAnomalyScore;
    private int identityVerificationScore;
    private int historicalFraudFlags;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDeviceRiskScore() {
        return deviceRiskScore;
    }

    public void setDeviceRiskScore(int deviceRiskScore) {
        this.deviceRiskScore = deviceRiskScore;
    }

    public int getTransactionAnomalyScore() {
        return transactionAnomalyScore;
    }

    public void setTransactionAnomalyScore(int transactionAnomalyScore) {
        this.transactionAnomalyScore = transactionAnomalyScore;
    }

    public int getIdentityVerificationScore() {
        return identityVerificationScore;
    }

    public void setIdentityVerificationScore(int identityVerificationScore) {
        this.identityVerificationScore = identityVerificationScore;
    }

    public int getHistoricalFraudFlags() {
        return historicalFraudFlags;
    }

    public void setHistoricalFraudFlags(int historicalFraudFlags) {
        this.historicalFraudFlags = historicalFraudFlags;
    }
}
