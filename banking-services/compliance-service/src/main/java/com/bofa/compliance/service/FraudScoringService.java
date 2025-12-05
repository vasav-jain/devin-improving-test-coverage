package com.bofa.compliance.service;

import com.bofa.compliance.dto.FraudProfile;
import org.springframework.stereotype.Service;

@Service
public class FraudScoringService {

    public int computeScore(FraudProfile profile) {
        int score = (int) (profile.getDeviceRiskScore() * 0.25
                + profile.getTransactionAnomalyScore() * 0.35
                + (100 - profile.getIdentityVerificationScore()) * 0.2
                + profile.getHistoricalFraudFlags() * 10);
        return Math.min(score, 100);
    }
}
