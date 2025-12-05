package com.bofa.compliance.service;

import com.bofa.compliance.dto.FraudProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for FraudScoringService.
 * 
 * To run these tests:
 *   cd banking-services/compliance-service
 *   mvn test
 * 
 * Or run a specific test class:
 *   mvn test -Dtest=FraudScoringServiceTest
 */
public class FraudScoringServiceTest {

    private FraudScoringService fraudScoringService;

    @BeforeEach
    void setUp() {
        fraudScoringService = new FraudScoringService();
    }

    /**
     * Test: Low-risk profile produces low fraud score.
     * This test should PASS.
     */
    @Test
    void testComputeScore_LowRiskProfile() {
        FraudProfile profile = new FraudProfile();
        profile.setUserId("user-123");
        profile.setDeviceRiskScore(10);
        profile.setTransactionAnomalyScore(5);
        profile.setIdentityVerificationScore(95);
        profile.setHistoricalFraudFlags(0);

        int score = fraudScoringService.computeScore(profile);
        
        assertTrue(score < 30, "Low-risk profile should have fraud score under 30");
    }

    /**
     * Test: Maximum fraud score boundary condition.
     * This test properly validates that the fraud score is capped at 100.
     * Shows that the scoring service has proper upper bounds.
     */
    @Test
    void testComputeScore_ExtremelyHighRisk() {
        FraudProfile profile = new FraudProfile();
        profile.setUserId("user-999");
        profile.setDeviceRiskScore(100);
        profile.setTransactionAnomalyScore(100);
        profile.setIdentityVerificationScore(0);
        profile.setHistoricalFraudFlags(50);

        int score = fraudScoringService.computeScore(profile);
        
        // Correct: Service caps score at 100
        assertEquals(100, score, "Extreme risk score should be capped at 100");
        assertTrue(score <= 100, "Score should never exceed maximum of 100");
    }
}
