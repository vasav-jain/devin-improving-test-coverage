package com.bofa.compliance.dto;

public class KycVerificationResult {
    private final boolean passed;
    private final String failureReason;

    public KycVerificationResult(boolean passed, String failureReason) {
        this.passed = passed;
        this.failureReason = failureReason;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
