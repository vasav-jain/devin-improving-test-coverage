package com.bofa.payments.service;

import com.bofa.payments.dto.AmortizationInstallment;
import com.bofa.payments.dto.MortgageEstimateRequest;
import com.bofa.payments.dto.MortgageEstimateResponse;
import com.bofa.payments.exception.PaymentValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for MortgageCalculator.
 * Tests cover mortgage estimation, PMI calculation, amortization schedules,
 * prepayment penalties, and all validation scenarios.
 */
public class MortgageCalculatorTest {

    private MortgageCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new MortgageCalculator();
    }

    /**
     * Tests basic mortgage estimation with valid inputs.
     * Verifies monthly payment calculation and response structure.
     */
    @Test
    void testEstimate_ValidRequest() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        assertNotNull(response);
        assertNotNull(response.getMonthlyPayment());
        assertTrue(response.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(response.getSchedule());
        assertFalse(response.getSchedule().isEmpty());
    }

    /**
     * Tests PMI is required when LTV exceeds 80%.
     * Validates PMI threshold calculation at 80% LTV.
     */
    @Test
    void testEstimate_PmiRequired_HighLTV() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(180000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(20000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertTrue(response.isPmiRequired(), "PMI should be required when LTV > 80%");
    }

    /**
     * Tests PMI is not required when LTV is at or below 80%.
     * Validates PMI threshold at exactly 80% LTV.
     */
    @Test
    void testEstimate_NoPmiRequired_LowLTV() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertFalse(response.isPmiRequired(), "PMI should not be required when LTV <= 80%");
    }

    /**
     * Tests PMI at exactly 80% LTV boundary.
     * Validates edge case at PMI threshold.
     */
    @Test
    void testEstimate_PmiThreshold_Exactly80Percent() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertFalse(response.isPmiRequired(), "PMI should not be required at exactly 80% LTV");
    }

    /**
     * Tests prepayment penalty is applied within first 24 months.
     * Validates prepayment penalty logic for early payoff.
     */
    @Test
    void testEstimate_PrepaymentPenalty_Within24Months() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(100000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(100000));
        request.setOptionalMonthlyPrepayment(BigDecimal.valueOf(1000));

        MortgageEstimateResponse response = calculator.estimate(request);

        List<AmortizationInstallment> schedule = response.getSchedule();
        assertFalse(schedule.isEmpty());
        
        boolean hasPenaltyInFirst24Months = schedule.stream()
                .filter(i -> i.getMonth() <= 24)
                .anyMatch(AmortizationInstallment::isPrepaymentPenaltyApplied);
        assertTrue(hasPenaltyInFirst24Months, "Prepayment penalty should apply within first 24 months");
    }

    /**
     * Tests no prepayment penalty after 24 months.
     * Validates prepayment penalty is not applied after grace period.
     */
    @Test
    void testEstimate_NoPrepaymentPenalty_After24Months() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(100000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(100000));
        request.setOptionalMonthlyPrepayment(BigDecimal.valueOf(100));

        MortgageEstimateResponse response = calculator.estimate(request);

        List<AmortizationInstallment> schedule = response.getSchedule();
        
        boolean hasPenaltyAfter24Months = schedule.stream()
                .filter(i -> i.getMonth() > 24)
                .anyMatch(AmortizationInstallment::isPrepaymentPenaltyApplied);
        assertFalse(hasPenaltyAfter24Months, "No prepayment penalty should apply after 24 months");
    }

    /**
     * Tests amortization schedule generation.
     * Validates schedule contains expected installments.
     */
    @Test
    void testEstimate_AmortizationSchedule() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        List<AmortizationInstallment> schedule = response.getSchedule();
        assertNotNull(schedule);
        assertFalse(schedule.isEmpty());
        
        AmortizationInstallment firstInstallment = schedule.get(0);
        assertEquals(1, firstInstallment.getMonth());
        assertNotNull(firstInstallment.getPrincipalComponent());
        assertNotNull(firstInstallment.getInterestComponent());
        assertNotNull(firstInstallment.getRemainingBalance());
    }

    /**
     * Tests that remaining balance decreases over time.
     * Validates amortization reduces principal.
     */
    @Test
    void testEstimate_BalanceDecreases() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        List<AmortizationInstallment> schedule = response.getSchedule();
        if (schedule.size() > 1) {
            BigDecimal firstBalance = schedule.get(0).getRemainingBalance();
            BigDecimal lastBalance = schedule.get(schedule.size() - 1).getRemainingBalance();
            assertTrue(lastBalance.compareTo(firstBalance) < 0, 
                    "Remaining balance should decrease over time");
        }
    }

    /**
     * Tests payoff month calculation.
     * Validates estimated months to payoff is returned.
     */
    @Test
    void testEstimate_PayoffMonth() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        assertTrue(response.getEstimatedMonthsToPayoff() > 0, 
                "Payoff month should be positive");
    }

    /**
     * Tests that null loan amount throws PaymentValidationException.
     * Validates null handling for loan amount.
     */
    @Test
    void testEstimate_NullLoanAmount() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(null);
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Loan amount must be at least 10,000"));
    }

    /**
     * Tests that loan amount below minimum throws PaymentValidationException.
     * Validates minimum loan amount of 10,000.
     */
    @Test
    void testEstimate_LoanAmountBelowMinimum() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(5000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(195000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Loan amount must be at least 10,000"));
    }

    /**
     * Tests that null annual rate throws PaymentValidationException.
     * Validates null handling for annual rate.
     */
    @Test
    void testEstimate_NullAnnualRate() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(null);
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that zero annual rate throws PaymentValidationException.
     * Validates rejection of zero rate.
     */
    @Test
    void testEstimate_ZeroAnnualRate() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.ZERO);
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that negative annual rate throws PaymentValidationException.
     * Validates rejection of negative rate.
     */
    @Test
    void testEstimate_NegativeAnnualRate() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(-5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Annual rate must be positive"));
    }

    /**
     * Tests that term below minimum (60 months) throws PaymentValidationException.
     * Validates minimum term of 60 months.
     */
    @Test
    void testEstimate_TermBelowMinimum() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(59);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Term must be between 60 and 480 months"));
    }

    /**
     * Tests that term above maximum (480 months) throws PaymentValidationException.
     * Validates maximum term of 480 months.
     */
    @Test
    void testEstimate_TermAboveMaximum() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(481);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Term must be between 60 and 480 months"));
    }

    /**
     * Tests minimum valid term (60 months).
     * Validates edge case at minimum term boundary.
     */
    @Test
    void testEstimate_MinimumValidTerm() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(100000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(60);
        request.setDownPayment(BigDecimal.valueOf(100000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertNotNull(response);
        assertNotNull(response.getMonthlyPayment());
    }

    /**
     * Tests maximum valid term (480 months).
     * Validates edge case at maximum term boundary.
     */
    @Test
    void testEstimate_MaximumValidTerm() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(100000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(480);
        request.setDownPayment(BigDecimal.valueOf(100000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertNotNull(response);
        assertNotNull(response.getMonthlyPayment());
    }

    /**
     * Tests that null property value throws PaymentValidationException.
     * Validates null handling for property value.
     */
    @Test
    void testEstimate_NullPropertyValue() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(null);
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Property value must exceed loan amount"));
    }

    /**
     * Tests that property value less than loan amount throws PaymentValidationException.
     * Validates property value must exceed loan amount.
     */
    @Test
    void testEstimate_PropertyValueLessThanLoan() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(200000));
        request.setPropertyValue(BigDecimal.valueOf(150000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(0));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> calculator.estimate(request)
        );

        assertTrue(exception.getMessage().contains("Property value must exceed loan amount"));
    }

    /**
     * Tests with zero prepayment.
     * Validates calculation works without optional prepayment.
     */
    @Test
    void testEstimate_ZeroPrepayment() {
        MortgageEstimateRequest request = createValidRequest();
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = calculator.estimate(request);

        assertNotNull(response);
        assertFalse(response.getSchedule().isEmpty());
    }

    /**
     * Tests with large prepayment that pays off loan quickly.
     * Validates early payoff scenario.
     */
    @Test
    void testEstimate_LargePrepayment_EarlyPayoff() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(50000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(150000));
        request.setOptionalMonthlyPrepayment(BigDecimal.valueOf(10000));

        MortgageEstimateResponse response = calculator.estimate(request);

        assertNotNull(response);
        assertTrue(response.getEstimatedMonthsToPayoff() < 360, 
                "Large prepayment should result in early payoff");
    }

    /**
     * Tests monthly payment is properly scaled to 2 decimal places.
     * Validates proper rounding of monthly payment.
     */
    @Test
    void testEstimate_MonthlyPaymentScale() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        assertEquals(2, response.getMonthlyPayment().scale(), 
                "Monthly payment should be scaled to 2 decimal places");
    }

    /**
     * Tests schedule installment values are properly scaled.
     * Validates proper rounding in amortization schedule.
     */
    @Test
    void testEstimate_ScheduleValuesScale() {
        MortgageEstimateRequest request = createValidRequest();

        MortgageEstimateResponse response = calculator.estimate(request);

        AmortizationInstallment installment = response.getSchedule().get(0);
        assertEquals(2, installment.getPrincipalComponent().scale());
        assertEquals(2, installment.getInterestComponent().scale());
        assertEquals(2, installment.getRemainingBalance().scale());
    }

    /**
     * Helper method to create a valid mortgage request.
     */
    private MortgageEstimateRequest createValidRequest() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);
        return request;
    }
}
