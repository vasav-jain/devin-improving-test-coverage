package com.bofa.compliance.dto;

import java.time.Instant;
import java.util.List;

public class ComplianceReport {
    private final String userId;
    private final Instant generatedAt;
    private final AmlResult lastAmlResult;
    private final KycVerificationResult kycResult;
    private final int fraudScore;
    private final String riskCategory;
    private final List<String> recommendations;

    public ComplianceReport(String userId,
                            Instant generatedAt,
                            AmlResult lastAmlResult,
                            KycVerificationResult kycResult,
                            int fraudScore,
                            String riskCategory,
                            List<String> recommendations) {
        this.userId = userId;
        this.generatedAt = generatedAt;
        this.lastAmlResult = lastAmlResult;
        this.kycResult = kycResult;
        this.fraudScore = fraudScore;
        this.riskCategory = riskCategory;
        this.recommendations = recommendations;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public AmlResult getLastAmlResult() {
        return lastAmlResult;
    }

    public KycVerificationResult getKycResult() {
        return kycResult;
    }

    public int getFraudScore() {
        return fraudScore;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
