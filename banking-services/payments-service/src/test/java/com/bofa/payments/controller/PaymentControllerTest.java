package com.bofa.payments.controller;

import com.bofa.payments.dto.*;
import com.bofa.payments.model.PaymentStatus;
import com.bofa.payments.repository.PaymentRepository;
import com.bofa.payments.service.InterestCalculator;
import com.bofa.payments.service.MortgageCalculator;
import com.bofa.payments.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PaymentController.
 * Tests cover all REST endpoints and their integration with PaymentService.
 */
public class PaymentControllerTest {

    private PaymentController controller;
    private PaymentService paymentService;
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
        InterestCalculator interestCalculator = new InterestCalculator();
        MortgageCalculator mortgageCalculator = new MortgageCalculator();
        paymentService = new PaymentService(paymentRepository, interestCalculator, mortgageCalculator);
        controller = new PaymentController(paymentService);
    }

    /**
     * Tests POST /payments/schedule endpoint with valid request.
     * Verifies payment is scheduled successfully.
     */
    @Test
    void testSchedulePayment_ValidRequest() {
        PaymentScheduleRequest request = createValidScheduleRequest();

        PaymentResponse response = controller.schedulePayment(request);

        assertNotNull(response);
        assertNotNull(response.getPaymentId());
        assertEquals(request.getAccountId(), response.getAccountId());
        assertEquals(request.getPrincipal(), response.getPrincipal());
        assertEquals(PaymentStatus.SCHEDULED, response.getStatus());
    }

    /**
     * Tests POST /payments/schedule returns correct scheduled date.
     * Verifies scheduled date is preserved.
     */
    @Test
    void testSchedulePayment_ScheduledDatePreserved() {
        PaymentScheduleRequest request = createValidScheduleRequest();
        LocalDate expectedDate = request.getScheduledDate();

        PaymentResponse response = controller.schedulePayment(request);

        assertEquals(expectedDate, response.getScheduledDate());
    }

    /**
     * Tests POST /payments/execute/{paymentId} endpoint with valid request.
     * Verifies payment is executed successfully.
     */
    @Test
    void testExecutePayment_ValidRequest() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = controller.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);

        PaymentResponse response = controller.executePayment(scheduled.getPaymentId(), execRequest);

        assertNotNull(response);
        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
        assertNotNull(response.getExecutedDate());
    }

    /**
     * Tests POST /payments/execute/{paymentId} with partial payment.
     * Verifies partial payment reduces principal by 50%.
     */
    @Test
    void testExecutePayment_PartialPayment() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = controller.schedulePayment(scheduleRequest);
        BigDecimal originalPrincipal = scheduled.getPrincipal();

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setPartialPayment(true);
        execRequest.setWaiveInterest(true);

        PaymentResponse response = controller.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(originalPrincipal.multiply(BigDecimal.valueOf(0.5)), response.getPrincipal());
    }

    /**
     * Tests POST /payments/execute/{paymentId} with failure reason.
     * Verifies failed payment is processed correctly.
     */
    @Test
    void testExecutePayment_WithFailureReason() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = controller.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setFailureReason("Insufficient funds");

        PaymentResponse response = controller.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    /**
     * Tests GET /payments/history/{accountId} endpoint.
     * Verifies payment history is returned correctly.
     */
    @Test
    void testGetPaymentHistory_ExistingAccount() {
        PaymentScheduleRequest request1 = createValidScheduleRequest();
        request1.setScheduledDate(LocalDate.now().plusDays(5));
        controller.schedulePayment(request1);

        PaymentScheduleRequest request2 = createValidScheduleRequest();
        request2.setScheduledDate(LocalDate.now().plusDays(10));
        controller.schedulePayment(request2);

        List<PaymentResponse> history = controller.getPaymentHistory(request1.getAccountId());

        assertNotNull(history);
        assertEquals(2, history.size());
    }

    /**
     * Tests GET /payments/history/{accountId} for non-existent account.
     * Verifies empty list is returned.
     */
    @Test
    void testGetPaymentHistory_NonExistentAccount() {
        List<PaymentResponse> history = controller.getPaymentHistory("NON-EXISTENT");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    /**
     * Tests POST /interest/calculate endpoint with valid request.
     * Verifies interest calculation is returned correctly.
     */
    @Test
    void testCalculateInterest_ValidRequest() {
        InterestCalculationRequest request = new InterestCalculationRequest();
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setAnnualRate(BigDecimal.valueOf(0.05));
        request.setDays(30);

        InterestCalculationResponse response = controller.calculateInterest(request);

        assertNotNull(response);
        assertNotNull(response.getAccruedInterest());
        assertNotNull(response.getTotalAmount());
    }

    /**
     * Tests POST /interest/calculate returns correct total amount.
     * Verifies total = principal + accrued interest.
     */
    @Test
    void testCalculateInterest_TotalAmountCalculation() {
        InterestCalculationRequest request = new InterestCalculationRequest();
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setAnnualRate(BigDecimal.valueOf(0.05));
        request.setDays(30);

        InterestCalculationResponse response = controller.calculateInterest(request);

        BigDecimal expectedTotal = request.getPrincipal().add(response.getAccruedInterest());
        assertEquals(expectedTotal, response.getTotalAmount());
    }

    /**
     * Tests POST /mortgage/estimate endpoint with valid request.
     * Verifies mortgage estimate is returned correctly.
     */
    @Test
    void testEstimateMortgage_ValidRequest() {
        MortgageEstimateRequest request = createValidMortgageRequest();

        MortgageEstimateResponse response = controller.estimateMortgage(request);

        assertNotNull(response);
        assertNotNull(response.getMonthlyPayment());
        assertTrue(response.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Tests POST /mortgage/estimate returns PMI status.
     * Verifies PMI required flag is set correctly.
     */
    @Test
    void testEstimateMortgage_PmiStatus() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(180000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(20000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = controller.estimateMortgage(request);

        assertTrue(response.isPmiRequired(), "PMI should be required for high LTV");
    }

    /**
     * Tests POST /mortgage/estimate returns amortization schedule.
     * Verifies schedule is included in response.
     */
    @Test
    void testEstimateMortgage_IncludesSchedule() {
        MortgageEstimateRequest request = createValidMortgageRequest();

        MortgageEstimateResponse response = controller.estimateMortgage(request);

        assertNotNull(response.getSchedule());
        assertFalse(response.getSchedule().isEmpty());
    }

    /**
     * Tests POST /mortgage/estimate returns payoff month.
     * Verifies estimated months to payoff is included.
     */
    @Test
    void testEstimateMortgage_IncludesPayoffMonth() {
        MortgageEstimateRequest request = createValidMortgageRequest();

        MortgageEstimateResponse response = controller.estimateMortgage(request);

        assertTrue(response.getEstimatedMonthsToPayoff() > 0);
    }

    /**
     * Tests controller constructor with PaymentService.
     * Verifies controller is properly initialized.
     */
    @Test
    void testConstructor_WithPaymentService() {
        PaymentController newController = new PaymentController(paymentService);

        assertNotNull(newController);
    }

    /**
     * Tests multiple payments can be scheduled for same account.
     * Verifies controller handles multiple requests correctly.
     */
    @Test
    void testSchedulePayment_MultipleForSameAccount() {
        PaymentScheduleRequest request1 = createValidScheduleRequest();
        request1.setScheduledDate(LocalDate.now().plusDays(5));
        PaymentResponse response1 = controller.schedulePayment(request1);

        PaymentScheduleRequest request2 = createValidScheduleRequest();
        request2.setScheduledDate(LocalDate.now().plusDays(10));
        PaymentResponse response2 = controller.schedulePayment(request2);

        assertNotEquals(response1.getPaymentId(), response2.getPaymentId());
        assertEquals(request1.getAccountId(), response1.getAccountId());
        assertEquals(request2.getAccountId(), response2.getAccountId());
    }

    /**
     * Helper method to create a valid payment schedule request.
     */
    private PaymentScheduleRequest createValidScheduleRequest() {
        PaymentScheduleRequest request = new PaymentScheduleRequest();
        request.setAccountId("ACC-001");
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setInterest(BigDecimal.valueOf(0.05));
        request.setScheduledDate(LocalDate.now().plusDays(5));
        return request;
    }

    /**
     * Helper method to create a valid mortgage estimate request.
     */
    private MortgageEstimateRequest createValidMortgageRequest() {
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
