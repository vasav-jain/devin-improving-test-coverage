package com.bofa.compliance.repository;

import com.bofa.compliance.dto.AmlResult;
import com.bofa.compliance.dto.KycVerificationResult;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ComplianceRepository {

    private final Map<String, AmlResult> amlResults = new ConcurrentHashMap<>();
    private final Map<String, KycVerificationResult> kycResults = new ConcurrentHashMap<>();
    private final Map<String, Integer> fraudScores = new ConcurrentHashMap<>();

    public void saveAml(String userId, AmlResult result) {
        amlResults.put(userId, result);
    }

    public void saveKyc(String userId, KycVerificationResult result) {
        kycResults.put(userId, result);
    }

    public void saveFraudScore(String userId, int score) {
        fraudScores.put(userId, score);
    }

    public AmlResult getAml(String userId) {
        return amlResults.get(userId);
    }

    public KycVerificationResult getKyc(String userId) {
        return kycResults.get(userId);
    }

    public Integer getFraudScore(String userId) {
        return fraudScores.get(userId);
    }
}
