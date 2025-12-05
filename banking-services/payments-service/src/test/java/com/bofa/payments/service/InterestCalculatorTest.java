package com.bofa.payments.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Simple tests for InterestCalculator.
 * 
 * To run these tests:
 *   cd banking-services/payments-service
 *   mvn test
 * 
 * Or run a specific test class:
 *   mvn test -Dtest=InterestCalculatorTest
 */
public class InterestCalculatorTest {

    private InterestCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new InterestCalculator();
    }

    /**
     * Test: Basic compound interest calculation with grace period.
     * This test should PASS.
     */
    @Test
    void testCalculateDailyCompound_WithGracePeriod() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 5;  // 3-day grace means only 2 chargeable days

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);
        
        assertNotNull(interest);
        assertTrue(interest.compareTo(BigDecimal.ZERO) > 0, "Interest should be positive after grace period");
    }

    /**
     * Test: Zero principal edge case with proper exception handling.
     * This test properly validates that the calculator rejects invalid input.
     * Shows best practice for exception testing.
     */
    @Test
    void testCalculateDailyCompound_ZeroPrincipal() {
        BigDecimal principal = BigDecimal.ZERO;
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 10;

        // Properly test that exception is thrown with expected message
        Exception exception = assertThrows(
            com.bofa.payments.exception.PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days),
            "Expected PaymentValidationException for zero principal"
        );

        // Verify the exception message is correct
        assertTrue(exception.getMessage().contains("Principal must be positive"),
            "Exception message should mention principal requirement");
    }
}
