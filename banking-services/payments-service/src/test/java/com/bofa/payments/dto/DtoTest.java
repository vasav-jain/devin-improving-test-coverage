package com.bofa.payments.dto;

import com.bofa.payments.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for DTO classes.
 * Tests cover all DTOs including requests, responses, and data transfer objects.
 */
public class DtoTest {

    /**
     * Tests PaymentScheduleRequest getters and setters.
     */
    @Test
    void testPaymentScheduleRequest_GettersSetters() {
        PaymentScheduleRequest request = new PaymentScheduleRequest();
        
        request.setAccountId("ACC-001");
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setInterest(BigDecimal.valueOf(50));
        request.setScheduledDate(LocalDate.of(2024, 6, 15));

        assertEquals("ACC-001", request.getAccountId());
        assertEquals(BigDecimal.valueOf(1000), request.getPrincipal());
        assertEquals(BigDecimal.valueOf(50), request.getInterest());
        assertEquals(LocalDate.of(2024, 6, 15), request.getScheduledDate());
    }

    /**
     * Tests PaymentExecutionRequest getters and setters.
     */
    @Test
    void testPaymentExecutionRequest_GettersSetters() {
        PaymentExecutionRequest request = new PaymentExecutionRequest();
        
        request.setExecutionDate(LocalDate.of(2024, 6, 15));
        request.setPartialPayment(true);
        request.setWaiveInterest(true);
        request.setFailureReason("Insufficient funds");

        assertEquals(LocalDate.of(2024, 6, 15), request.getExecutionDate());
        assertTrue(request.isPartialPayment());
        assertTrue(request.isWaiveInterest());
        assertEquals("Insufficient funds", request.getFailureReason());
    }

    /**
     * Tests PaymentExecutionRequest default boolean values.
     */
    @Test
    void testPaymentExecutionRequest_DefaultBooleans() {
        PaymentExecutionRequest request = new PaymentExecutionRequest();

        assertFalse(request.isPartialPayment());
        assertFalse(request.isWaiveInterest());
    }

    /**
     * Tests PaymentResponse constructor and getters.
     */
    @Test
    void testPaymentResponse_ConstructorAndGetters() {
        PaymentResponse response = new PaymentResponse(
                "PAY-001",
                "ACC-001",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.of(2024, 6, 15),
                LocalDate.of(2024, 6, 15),
                PaymentStatus.EXECUTED
        );

        assertEquals("PAY-001", response.getPaymentId());
        assertEquals("ACC-001", response.getAccountId());
        assertEquals(BigDecimal.valueOf(1000), response.getPrincipal());
        assertEquals(BigDecimal.valueOf(50), response.getInterest());
        assertEquals(LocalDate.of(2024, 6, 15), response.getScheduledDate());
        assertEquals(LocalDate.of(2024, 6, 15), response.getExecutedDate());
        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
    }

    /**
     * Tests PaymentResponse with null executed date.
     */
    @Test
    void testPaymentResponse_NullExecutedDate() {
        PaymentResponse response = new PaymentResponse(
                "PAY-001",
                "ACC-001",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.of(2024, 6, 15),
                null,
                PaymentStatus.SCHEDULED
        );

        assertNull(response.getExecutedDate());
    }

    /**
     * Tests InterestCalculationRequest getters and setters.
     */
    @Test
    void testInterestCalculationRequest_GettersSetters() {
        InterestCalculationRequest request = new InterestCalculationRequest();
        
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setAnnualRate(BigDecimal.valueOf(0.05));
        request.setDays(30);

        assertEquals(BigDecimal.valueOf(1000), request.getPrincipal());
        assertEquals(BigDecimal.valueOf(0.05), request.getAnnualRate());
        assertEquals(30, request.getDays());
    }

    /**
     * Tests InterestCalculationResponse constructor and getters.
     */
    @Test
    void testInterestCalculationResponse_ConstructorAndGetters() {
        InterestCalculationResponse response = new InterestCalculationResponse(
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1050)
        );

        assertEquals(BigDecimal.valueOf(50), response.getAccruedInterest());
        assertEquals(BigDecimal.valueOf(1050), response.getTotalAmount());
    }

    /**
     * Tests MortgageEstimateRequest getters and setters.
     */
    @Test
    void testMortgageEstimateRequest_GettersSetters() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        
        request.setLoanAmount(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setPropertyValue(BigDecimal.valueOf(250000));
        request.setDownPayment(BigDecimal.valueOf(50000));
        request.setOptionalMonthlyPrepayment(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(200000), request.getLoanAmount());
        assertEquals(BigDecimal.valueOf(5.0), request.getAnnualRate());
        assertEquals(360, request.getTermMonths());
        assertEquals(BigDecimal.valueOf(250000), request.getPropertyValue());
        assertEquals(BigDecimal.valueOf(50000), request.getDownPayment());
        assertEquals(BigDecimal.valueOf(500), request.getOptionalMonthlyPrepayment());
    }

    /**
     * Tests MortgageEstimateRequest default prepayment value.
     */
    @Test
    void testMortgageEstimateRequest_DefaultPrepayment() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();

        assertEquals(BigDecimal.ZERO, request.getOptionalMonthlyPrepayment());
    }

    /**
     * Tests MortgageEstimateResponse constructor and getters.
     */
    @Test
    void testMortgageEstimateResponse_ConstructorAndGetters() {
        List<AmortizationInstallment> schedule = new ArrayList<>();
        schedule.add(new AmortizationInstallment(1, BigDecimal.valueOf(500), 
                BigDecimal.valueOf(833), BigDecimal.valueOf(199500), false));

        MortgageEstimateResponse response = new MortgageEstimateResponse(
                BigDecimal.valueOf(1073.64),
                true,
                360,
                schedule
        );

        assertEquals(BigDecimal.valueOf(1073.64), response.getMonthlyPayment());
        assertTrue(response.isPmiRequired());
        assertEquals(360, response.getEstimatedMonthsToPayoff());
        assertEquals(1, response.getSchedule().size());
    }

    /**
     * Tests MortgageEstimateResponse with empty schedule.
     */
    @Test
    void testMortgageEstimateResponse_EmptySchedule() {
        MortgageEstimateResponse response = new MortgageEstimateResponse(
                BigDecimal.valueOf(1000),
                false,
                0,
                new ArrayList<>()
        );

        assertTrue(response.getSchedule().isEmpty());
    }

    /**
     * Tests AmortizationInstallment constructor and getters.
     */
    @Test
    void testAmortizationInstallment_ConstructorAndGetters() {
        AmortizationInstallment installment = new AmortizationInstallment(
                12,
                BigDecimal.valueOf(600),
                BigDecimal.valueOf(750),
                BigDecimal.valueOf(185000),
                true
        );

        assertEquals(12, installment.getMonth());
        assertEquals(BigDecimal.valueOf(600), installment.getPrincipalComponent());
        assertEquals(BigDecimal.valueOf(750), installment.getInterestComponent());
        assertEquals(BigDecimal.valueOf(185000), installment.getRemainingBalance());
        assertTrue(installment.isPrepaymentPenaltyApplied());
    }

    /**
     * Tests AmortizationInstallment without prepayment penalty.
     */
    @Test
    void testAmortizationInstallment_NoPenalty() {
        AmortizationInstallment installment = new AmortizationInstallment(
                25,
                BigDecimal.valueOf(700),
                BigDecimal.valueOf(650),
                BigDecimal.valueOf(170000),
                false
        );

        assertFalse(installment.isPrepaymentPenaltyApplied());
    }

    /**
     * Tests AmortizationInstallment first month.
     */
    @Test
    void testAmortizationInstallment_FirstMonth() {
        AmortizationInstallment installment = new AmortizationInstallment(
                1,
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(199600),
                false
        );

        assertEquals(1, installment.getMonth());
    }

    /**
     * Tests AmortizationInstallment last month with zero balance.
     */
    @Test
    void testAmortizationInstallment_LastMonth() {
        AmortizationInstallment installment = new AmortizationInstallment(
                360,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5),
                BigDecimal.ZERO,
                false
        );

        assertEquals(360, installment.getMonth());
        assertEquals(BigDecimal.ZERO, installment.getRemainingBalance());
    }
}
