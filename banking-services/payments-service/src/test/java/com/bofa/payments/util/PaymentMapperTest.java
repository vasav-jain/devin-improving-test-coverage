package com.bofa.payments.util;

import com.bofa.payments.dto.PaymentResponse;
import com.bofa.payments.model.Payment;
import com.bofa.payments.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PaymentMapper.
 * Tests cover mapping from Payment entity to PaymentResponse DTO.
 */
public class PaymentMapperTest {

    /**
     * Tests mapping a complete Payment to PaymentResponse.
     * Verifies all fields are correctly mapped.
     */
    @Test
    void testToResponse_CompletePayment() {
        Payment payment = new Payment(
                "PAY-001",
                "ACC-001",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 15),
                PaymentStatus.EXECUTED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertNotNull(response);
        assertEquals("PAY-001", response.getPaymentId());
        assertEquals("ACC-001", response.getAccountId());
        assertEquals(BigDecimal.valueOf(1000), response.getPrincipal());
        assertEquals(BigDecimal.valueOf(50), response.getInterest());
        assertEquals(LocalDate.of(2024, 1, 15), response.getScheduledDate());
        assertEquals(LocalDate.of(2024, 1, 15), response.getExecutedDate());
        assertEquals(PaymentStatus.EXECUTED, response.getStatus());
    }

    /**
     * Tests mapping a Payment with null executed date.
     * Verifies scheduled payment is mapped correctly.
     */
    @Test
    void testToResponse_ScheduledPayment() {
        Payment payment = new Payment(
                "PAY-002",
                "ACC-002",
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(100),
                LocalDate.of(2024, 2, 20),
                null,
                PaymentStatus.SCHEDULED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertNotNull(response);
        assertEquals("PAY-002", response.getPaymentId());
        assertNull(response.getExecutedDate());
        assertEquals(PaymentStatus.SCHEDULED, response.getStatus());
    }

    /**
     * Tests mapping a Payment with FAILED status.
     * Verifies failed payment is mapped correctly.
     */
    @Test
    void testToResponse_FailedPayment() {
        Payment payment = new Payment(
                "PAY-003",
                "ACC-003",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(25),
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 3, 10),
                PaymentStatus.FAILED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    /**
     * Tests mapping a Payment with PARTIAL status.
     * Verifies partial payment is mapped correctly.
     */
    @Test
    void testToResponse_PartialPayment() {
        Payment payment = new Payment(
                "PAY-004",
                "ACC-004",
                BigDecimal.valueOf(750),
                BigDecimal.valueOf(37.50),
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 5),
                PaymentStatus.PARTIAL
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(PaymentStatus.PARTIAL, response.getStatus());
        assertEquals(BigDecimal.valueOf(750), response.getPrincipal());
    }

    /**
     * Tests mapping a Payment with CANCELLED status.
     * Verifies cancelled payment is mapped correctly.
     */
    @Test
    void testToResponse_CancelledPayment() {
        Payment payment = new Payment(
                "PAY-005",
                "ACC-005",
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(150),
                LocalDate.of(2024, 5, 1),
                null,
                PaymentStatus.CANCELLED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(PaymentStatus.CANCELLED, response.getStatus());
    }

    /**
     * Tests mapping preserves BigDecimal precision.
     * Verifies decimal values are not altered.
     */
    @Test
    void testToResponse_PreservesBigDecimalPrecision() {
        BigDecimal principal = new BigDecimal("1234.56");
        BigDecimal interest = new BigDecimal("67.89");
        
        Payment payment = new Payment(
                "PAY-006",
                "ACC-006",
                principal,
                interest,
                LocalDate.now(),
                null,
                PaymentStatus.SCHEDULED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(principal, response.getPrincipal());
        assertEquals(interest, response.getInterest());
    }

    /**
     * Tests mapping with zero principal.
     * Verifies edge case with zero value.
     */
    @Test
    void testToResponse_ZeroPrincipal() {
        Payment payment = new Payment(
                "PAY-007",
                "ACC-007",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDate.now(),
                null,
                PaymentStatus.SCHEDULED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(BigDecimal.ZERO, response.getPrincipal());
        assertEquals(BigDecimal.ZERO, response.getInterest());
    }

    /**
     * Tests mapping with large principal value.
     * Verifies large values are handled correctly.
     */
    @Test
    void testToResponse_LargePrincipal() {
        BigDecimal largePrincipal = BigDecimal.valueOf(999999999.99);
        
        Payment payment = new Payment(
                "PAY-008",
                "ACC-008",
                largePrincipal,
                BigDecimal.valueOf(1000),
                LocalDate.now(),
                null,
                PaymentStatus.SCHEDULED
        );

        PaymentResponse response = PaymentMapper.toResponse(payment);

        assertEquals(largePrincipal, response.getPrincipal());
    }
}
