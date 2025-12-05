package com.bofa.payments.service;

import com.bofa.payments.exception.PaymentValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for InterestCalculator.
 * Tests cover all business logic paths, edge cases, and validation scenarios.
 */
public class InterestCalculatorTest {

    private InterestCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new InterestCalculator();
    }

    /**
     * Tests basic compound interest calculation with days beyond grace period.
     * Verifies that interest is calculated correctly when chargeable days exist.
     */
    @Test
    void testCalculateDailyCompound_WithGracePeriod() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 5;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertNotNull(interest);
        assertTrue(interest.compareTo(BigDecimal.ZERO) > 0, "Interest should be positive after grace period");
    }

    /**
     * Tests that zero principal throws PaymentValidationException.
     * Validates rejection of invalid principal values.
     */
    @Test
    void testCalculateDailyCompound_ZeroPrincipal() {
        BigDecimal principal = BigDecimal.ZERO;
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days),
            "Expected PaymentValidationException for zero principal"
        );

        assertTrue(exception.getMessage().contains("Principal must be positive"),
            "Exception message should mention principal requirement");
    }

    /**
     * Tests that negative principal throws PaymentValidationException.
     * Validates rejection of negative principal values.
     */
    @Test
    void testCalculateDailyCompound_NegativePrincipal() {
        BigDecimal principal = BigDecimal.valueOf(-1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Principal must be positive"));
    }

    /**
     * Tests that null principal throws PaymentValidationException.
     * Validates null handling for principal parameter.
     */
    @Test
    void testCalculateDailyCompound_NullPrincipal() {
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(null, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Principal must be positive"));
    }

    /**
     * Tests that zero annual rate throws PaymentValidationException.
     * Validates rejection of zero rate values.
     */
    @Test
    void testCalculateDailyCompound_ZeroRate() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.ZERO;
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that negative annual rate throws PaymentValidationException.
     * Validates rejection of negative rate values.
     */
    @Test
    void testCalculateDailyCompound_NegativeRate() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(-0.05);
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that null annual rate throws PaymentValidationException.
     * Validates null handling for rate parameter.
     */
    @Test
    void testCalculateDailyCompound_NullRate() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        int days = 10;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, null, days)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that negative days throws PaymentValidationException.
     * Validates rejection of negative day values.
     */
    @Test
    void testCalculateDailyCompound_NegativeDays() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = -1;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Days must be between 0 and 3650"));
    }

    /**
     * Tests that days exceeding maximum (3650) throws PaymentValidationException.
     * Validates upper boundary for days parameter.
     */
    @Test
    void testCalculateDailyCompound_DaysExceedMax() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 3651;

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.calculateDailyCompound(principal, annualRate, days)
        );

        assertTrue(exception.getMessage().contains("Days must be between 0 and 3650"));
    }

    /**
     * Tests that zero days returns zero interest.
     * Validates edge case where no time has passed.
     */
    @Test
    void testCalculateDailyCompound_ZeroDays() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 0;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertEquals(BigDecimal.ZERO, interest);
    }

    /**
     * Tests that days within grace period (1-3 days) returns zero interest.
     * Validates the 3-day grace period logic.
     */
    @Test
    void testCalculateDailyCompound_WithinGracePeriod() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);

        assertEquals(BigDecimal.ZERO, calculator.calculateDailyCompound(principal, annualRate, 1));
        assertEquals(BigDecimal.ZERO, calculator.calculateDailyCompound(principal, annualRate, 2));
        assertEquals(BigDecimal.ZERO, calculator.calculateDailyCompound(principal, annualRate, 3));
    }

    /**
     * Tests exactly at grace period boundary (3 days).
     * Validates that exactly 3 days results in zero chargeable days.
     */
    @Test
    void testCalculateDailyCompound_ExactlyAtGracePeriodBoundary() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 3;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertEquals(BigDecimal.ZERO, interest);
    }

    /**
     * Tests one day beyond grace period (4 days = 1 chargeable day).
     * Validates that interest starts accruing after grace period.
     */
    @Test
    void testCalculateDailyCompound_OneDayBeyondGracePeriod() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 4;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertNotNull(interest);
        assertTrue(interest.compareTo(BigDecimal.ZERO) > 0, "Interest should be positive for 1 chargeable day");
    }

    /**
     * Tests maximum allowed days (3650).
     * Validates upper boundary calculation works correctly.
     */
    @Test
    void testCalculateDailyCompound_MaxDays() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 3650;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertNotNull(interest);
        assertTrue(interest.compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Tests with large principal value.
     * Validates calculation handles large amounts correctly.
     */
    @Test
    void testCalculateDailyCompound_LargePrincipal() {
        BigDecimal principal = BigDecimal.valueOf(1000000000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 365;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertNotNull(interest);
        assertTrue(interest.compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Tests that result is properly scaled to 2 decimal places.
     * Validates proper rounding of interest calculation.
     */
    @Test
    void testCalculateDailyCompound_ResultScale() {
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(0.05);
        int days = 30;

        BigDecimal interest = calculator.calculateDailyCompound(principal, annualRate, days);

        assertEquals(2, interest.scale(), "Interest should be scaled to 2 decimal places");
    }
}
