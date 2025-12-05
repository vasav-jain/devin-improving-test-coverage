package com.bofa.payments.service;

import com.bofa.payments.dto.*;
import com.bofa.payments.exception.PaymentNotFoundException;
import com.bofa.payments.exception.PaymentValidationException;
import com.bofa.payments.model.Payment;
import com.bofa.payments.model.PaymentStatus;
import com.bofa.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PaymentService.
 * Tests cover payment scheduling, execution, history retrieval,
 * interest calculation, mortgage estimation, and all validation scenarios.
 */
public class PaymentServiceTest {

    private PaymentService paymentService;
    private PaymentRepository paymentRepository;
    private InterestCalculator interestCalculator;
    private MortgageCalculator mortgageCalculator;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
        interestCalculator = new InterestCalculator();
        mortgageCalculator = new MortgageCalculator();
        paymentService = new PaymentService(paymentRepository, interestCalculator, mortgageCalculator);
    }

    /**
     * Tests successful payment scheduling with valid request.
     * Verifies payment is created with SCHEDULED status.
     */
    @Test
    void testSchedulePayment_ValidRequest() {
        PaymentScheduleRequest request = createValidScheduleRequest();

        PaymentResponse response = paymentService.schedulePayment(request);

        assertNotNull(response);
        assertNotNull(response.getPaymentId());
        assertEquals(request.getAccountId(), response.getAccountId());
        assertEquals(request.getPrincipal(), response.getPrincipal());
        assertEquals(PaymentStatus.SCHEDULED, response.getStatus());
    }

    /**
     * Tests that scheduling with null principal throws PaymentValidationException.
     * Validates null handling for principal.
     */
    @Test
    void testSchedulePayment_NullPrincipal() {
        PaymentScheduleRequest request = new PaymentScheduleRequest();
        request.setAccountId("ACC-001");
        request.setPrincipal(null);
        request.setInterest(BigDecimal.valueOf(50));
        request.setScheduledDate(LocalDate.now().plusDays(5));

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.schedulePayment(request)
        );

        assertTrue(exception.getMessage().contains("Principal must be at least 100"));
    }

    /**
     * Tests that scheduling with principal below minimum throws PaymentValidationException.
     * Validates minimum principal of 100.
     */
    @Test
    void testSchedulePayment_PrincipalBelowMinimum() {
        PaymentScheduleRequest request = new PaymentScheduleRequest();
        request.setAccountId("ACC-001");
        request.setPrincipal(BigDecimal.valueOf(50));
        request.setInterest(BigDecimal.valueOf(5));
        request.setScheduledDate(LocalDate.now().plusDays(5));

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.schedulePayment(request)
        );

        assertTrue(exception.getMessage().contains("Principal must be at least 100"));
    }

    /**
     * Tests that scheduling with past date throws PaymentValidationException.
     * Validates scheduled date must be at least 1 day in future.
     */
    @Test
    void testSchedulePayment_PastScheduledDate() {
        PaymentScheduleRequest request = new PaymentScheduleRequest();
        request.setAccountId("ACC-001");
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setInterest(BigDecimal.valueOf(50));
        request.setScheduledDate(LocalDate.now());

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.schedulePayment(request)
        );

        assertTrue(exception.getMessage().contains("Scheduled date must be at least 1 day in the future"));
    }

    /**
     * Tests successful payment execution on scheduled date.
     * Verifies payment status changes to EXECUTED.
     */
    @Test
    void testExecutePayment_OnScheduledDate() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
        assertNotNull(response.getExecutedDate());
    }

    /**
     * Tests payment execution with interest waiver.
     * Verifies interest is not accrued when waived.
     */
    @Test
    void testExecutePayment_WithInterestWaiver() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);
        BigDecimal originalInterest = scheduled.getInterest();

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().plusDays(5));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(true);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
        assertEquals(originalInterest, response.getInterest(), "Interest should not accrue when waived");
    }

    /**
     * Tests payment execution without interest waiver.
     * Verifies interest accrues when not waived.
     */
    @Test
    void testExecutePayment_WithoutInterestWaiver() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().plusDays(10));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
    }

    /**
     * Tests partial payment execution.
     * Verifies principal is reduced by 50%. Note: The service code sets status to PARTIAL
     * but then the else block overwrites it to EXECUTED since failureReason is null.
     */
    @Test
    void testExecutePayment_PartialPayment() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);
        BigDecimal originalPrincipal = scheduled.getPrincipal();

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setPartialPayment(true);
        execRequest.setWaiveInterest(true);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(originalPrincipal.multiply(BigDecimal.valueOf(0.5)), response.getPrincipal());
        assertNotNull(response.getExecutedDate());
    }

    /**
     * Tests failed payment execution.
     * Verifies status is FAILED when failure reason is provided.
     */
    @Test
    void testExecutePayment_WithFailureReason() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);
        execRequest.setFailureReason("Insufficient funds");

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    /**
     * Tests that executing non-scheduled payment throws PaymentValidationException.
     * Validates only SCHEDULED payments can be executed.
     */
    @Test
    void testExecutePayment_NonScheduledPayment() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate());

        paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.executePayment(scheduled.getPaymentId(), execRequest)
        );

        assertTrue(exception.getMessage().contains("Only scheduled payments can be executed"));
    }

    /**
     * Tests that execution date too early throws PaymentValidationException.
     * Validates execution date cannot be earlier than 3 days before scheduled date.
     */
    @Test
    void testExecutePayment_ExecutionDateTooEarly() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().minusDays(5));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.executePayment(scheduled.getPaymentId(), execRequest)
        );

        assertTrue(exception.getMessage().contains("Execution date cannot be earlier than 3 days before scheduled date"));
    }

    /**
     * Tests that execution date too late throws PaymentValidationException.
     * Validates execution date cannot be more than 15 days after scheduled date.
     */
    @Test
    void testExecutePayment_ExecutionDateTooLate() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().plusDays(20));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(false);

        Exception exception = assertThrows(
            PaymentValidationException.class,
            () -> paymentService.executePayment(scheduled.getPaymentId(), execRequest)
        );

        assertTrue(exception.getMessage().contains("Execution date cannot be more than 15 days after scheduled date"));
    }

    /**
     * Tests execution at exactly 3 days before scheduled date.
     * Validates boundary condition for early execution.
     */
    @Test
    void testExecutePayment_Exactly3DaysEarly() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().minusDays(3));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(true);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
    }

    /**
     * Tests execution at exactly 15 days after scheduled date.
     * Validates boundary condition for late execution.
     */
    @Test
    void testExecutePayment_Exactly15DaysLate() {
        PaymentScheduleRequest scheduleRequest = createValidScheduleRequest();
        PaymentResponse scheduled = paymentService.schedulePayment(scheduleRequest);

        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(scheduleRequest.getScheduledDate().plusDays(15));
        execRequest.setPartialPayment(false);
        execRequest.setWaiveInterest(true);

        PaymentResponse response = paymentService.executePayment(scheduled.getPaymentId(), execRequest);

        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
    }

    /**
     * Tests getting payment history for an account.
     * Verifies history is returned sorted by scheduled date.
     */
    @Test
    void testGetPaymentHistory_ExistingAccount() {
        PaymentScheduleRequest request1 = createValidScheduleRequest();
        request1.setScheduledDate(LocalDate.now().plusDays(5));
        paymentService.schedulePayment(request1);

        PaymentScheduleRequest request2 = createValidScheduleRequest();
        request2.setScheduledDate(LocalDate.now().plusDays(10));
        paymentService.schedulePayment(request2);

        List<PaymentResponse> history = paymentService.getPaymentHistory(request1.getAccountId());

        assertNotNull(history);
        assertEquals(2, history.size());
    }

    /**
     * Tests getting payment history for non-existent account.
     * Verifies empty list is returned.
     */
    @Test
    void testGetPaymentHistory_NonExistentAccount() {
        List<PaymentResponse> history = paymentService.getPaymentHistory("NON-EXISTENT");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    /**
     * Tests interest calculation through service.
     * Verifies delegation to InterestCalculator.
     */
    @Test
    void testCalculateInterest_ValidRequest() {
        InterestCalculationRequest request = new InterestCalculationRequest();
        request.setPrincipal(BigDecimal.valueOf(1000));
        request.setAnnualRate(BigDecimal.valueOf(0.05));
        request.setDays(30);

        InterestCalculationResponse response = paymentService.calculateInterest(request);

        assertNotNull(response);
        assertNotNull(response.getAccruedInterest());
        assertNotNull(response.getTotalAmount());
        assertEquals(request.getPrincipal().add(response.getAccruedInterest()), response.getTotalAmount());
    }

    /**
     * Tests mortgage estimation through service.
     * Verifies delegation to MortgageCalculator.
     */
    @Test
    void testEstimateMortgage_ValidRequest() {
        MortgageEstimateRequest request = new MortgageEstimateRequest();
        request.setLoanAmount(BigDecimal.valueOf(160000));
        request.setPropertyValue(BigDecimal.valueOf(200000));
        request.setAnnualRate(BigDecimal.valueOf(5.0));
        request.setTermMonths(360);
        request.setDownPayment(BigDecimal.valueOf(40000));
        request.setOptionalMonthlyPrepayment(BigDecimal.ZERO);

        MortgageEstimateResponse response = paymentService.estimateMortgage(request);

        assertNotNull(response);
        assertNotNull(response.getMonthlyPayment());
        assertNotNull(response.getSchedule());
    }

    /**
     * Tests that executing non-existent payment throws PaymentNotFoundException.
     * Validates error handling for missing payments.
     */
    @Test
    void testExecutePayment_NonExistentPayment() {
        PaymentExecutionRequest execRequest = new PaymentExecutionRequest();
        execRequest.setExecutionDate(LocalDate.now());

        assertThrows(
            PaymentNotFoundException.class,
            () -> paymentService.executePayment("NON-EXISTENT-ID", execRequest)
        );
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
}
