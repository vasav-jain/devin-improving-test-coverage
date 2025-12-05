package com.bofa.compliance.service;

import com.bofa.compliance.dto.*;
import com.bofa.compliance.repository.ComplianceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplianceService {

    private final AmlRuleEngine amlRuleEngine;
    private final KycVerificationService kycVerificationService;
    private final FraudScoringService fraudScoringService;
    private final ComplianceRepository complianceRepository;

    public ComplianceService(AmlRuleEngine amlRuleEngine,
                             KycVerificationService kycVerificationService,
                             FraudScoringService fraudScoringService,
                             ComplianceRepository complianceRepository) {
        this.amlRuleEngine = amlRuleEngine;
        this.kycVerificationService = kycVerificationService;
        this.fraudScoringService = fraudScoringService;
        this.complianceRepository = complianceRepository;
    }

    public AmlResult runAmlCheck(TransactionProfile profile) {
        AmlResult result = amlRuleEngine.runRules(profile);
        complianceRepository.saveAml(profile.getUserId(), result);
        return result;
    }

    public KycVerificationResult verifyKyc(KycVerificationRequest request) {
        KycVerificationResult result = kycVerificationService.verifyKyc(request);
        complianceRepository.saveKyc(request.getUserId(), result);
        return result;
    }

    public int calculateFraudScore(FraudProfile profile) {
        int score = fraudScoringService.computeScore(profile);
        complianceRepository.saveFraudScore(profile.getUserId(), score);
        return score;
    }

    public ComplianceReport generateComplianceReport(String userId) {
        AmlResult aml = complianceRepository.getAml(userId);
        KycVerificationResult kyc = complianceRepository.getKyc(userId);
        Integer fraud = complianceRepository.getFraudScore(userId);

        String category = classifyRisk(aml, kyc, fraud);
        List<String> recommendations = new ArrayList<>();
        if (aml != null && aml.isFlagged()) {
            recommendations.add("Escalate AML review to Tier 2 analyst");
        }
        if (kyc != null && !kyc.isPassed()) {
            recommendations.add("Request additional identity documentation");
        }
        if (fraud != null && fraud > 70) {
            recommendations.add("Enable step-up authentication for next login");
        }

        return new ComplianceReport(userId, Instant.now(), aml, kyc, fraud == null ? 0 : fraud, category, recommendations);
    }

    private String classifyRisk(AmlResult aml, KycVerificationResult kyc, Integer fraudScore) {
        if ((aml != null && aml.getScore() > 80) || (fraudScore != null && fraudScore > 80)) {
            return "CRITICAL";
        }
        if ((kyc != null && !kyc.isPassed()) || (aml != null && aml.isFlagged())) {
            return "HIGH";
        }
        if (fraudScore != null && fraudScore > 60) {
            return "ELEVATED";
        }
        return "LOW";
    }
}
