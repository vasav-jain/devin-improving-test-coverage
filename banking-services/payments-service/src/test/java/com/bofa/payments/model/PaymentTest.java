package com.bofa.payments.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Payment model.
 * Tests cover constructors, getters, setters, equals, and hashCode.
 */
public class PaymentTest {

    /**
     * Tests all-args constructor creates Payment with correct values.
     * Verifies all fields are set correctly.
     */
    @Test
    void testConstructor_AllArgs() {
        String paymentId = "PAY-001";
        String accountId = "ACC-001";
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal interest = BigDecimal.valueOf(50);
        LocalDate scheduledDate = LocalDate.of(2024, 1, 15);
        LocalDate executedDate = LocalDate.of(2024, 1, 15);
        PaymentStatus status = PaymentStatus.EXECUTED;

        Payment payment = new Payment(paymentId, accountId, principal, interest, 
                scheduledDate, executedDate, status);

        assertEquals(paymentId, payment.getPaymentId());
        assertEquals(accountId, payment.getAccountId());
        assertEquals(principal, payment.getPrincipal());
        assertEquals(interest, payment.getInterest());
        assertEquals(scheduledDate, payment.getScheduledDate());
        assertEquals(executedDate, payment.getExecutedDate());
        assertEquals(status, payment.getStatus());
    }

    /**
     * Tests no-args constructor creates Payment with null values.
     * Verifies default constructor works.
     */
    @Test
    void testConstructor_NoArgs() {
        Payment payment = new Payment();

        assertNull(payment.getPaymentId());
        assertNull(payment.getAccountId());
        assertNull(payment.getPrincipal());
        assertNull(payment.getInterest());
        assertNull(payment.getScheduledDate());
        assertNull(payment.getExecutedDate());
        assertNull(payment.getStatus());
    }

    /**
     * Tests setPaymentId and getPaymentId.
     * Verifies paymentId getter and setter work correctly.
     */
    @Test
    void testSetGetPaymentId() {
        Payment payment = new Payment();
        String paymentId = "PAY-123";

        payment.setPaymentId(paymentId);

        assertEquals(paymentId, payment.getPaymentId());
    }

    /**
     * Tests setAccountId and getAccountId.
     * Verifies accountId getter and setter work correctly.
     */
    @Test
    void testSetGetAccountId() {
        Payment payment = new Payment();
        String accountId = "ACC-456";

        payment.setAccountId(accountId);

        assertEquals(accountId, payment.getAccountId());
    }

    /**
     * Tests setPrincipal and getPrincipal.
     * Verifies principal getter and setter work correctly.
     */
    @Test
    void testSetGetPrincipal() {
        Payment payment = new Payment();
        BigDecimal principal = BigDecimal.valueOf(5000);

        payment.setPrincipal(principal);

        assertEquals(principal, payment.getPrincipal());
    }

    /**
     * Tests setInterest and getInterest.
     * Verifies interest getter and setter work correctly.
     */
    @Test
    void testSetGetInterest() {
        Payment payment = new Payment();
        BigDecimal interest = BigDecimal.valueOf(250);

        payment.setInterest(interest);

        assertEquals(interest, payment.getInterest());
    }

    /**
     * Tests setScheduledDate and getScheduledDate.
     * Verifies scheduledDate getter and setter work correctly.
     */
    @Test
    void testSetGetScheduledDate() {
        Payment payment = new Payment();
        LocalDate scheduledDate = LocalDate.of(2024, 6, 15);

        payment.setScheduledDate(scheduledDate);

        assertEquals(scheduledDate, payment.getScheduledDate());
    }

    /**
     * Tests setExecutedDate and getExecutedDate.
     * Verifies executedDate getter and setter work correctly.
     */
    @Test
    void testSetGetExecutedDate() {
        Payment payment = new Payment();
        LocalDate executedDate = LocalDate.of(2024, 6, 15);

        payment.setExecutedDate(executedDate);

        assertEquals(executedDate, payment.getExecutedDate());
    }

    /**
     * Tests setStatus and getStatus.
     * Verifies status getter and setter work correctly.
     */
    @Test
    void testSetGetStatus() {
        Payment payment = new Payment();

        payment.setStatus(PaymentStatus.SCHEDULED);
        assertEquals(PaymentStatus.SCHEDULED, payment.getStatus());

        payment.setStatus(PaymentStatus.EXECUTED);
        assertEquals(PaymentStatus.EXECUTED, payment.getStatus());

        payment.setStatus(PaymentStatus.FAILED);
        assertEquals(PaymentStatus.FAILED, payment.getStatus());

        payment.setStatus(PaymentStatus.PARTIAL);
        assertEquals(PaymentStatus.PARTIAL, payment.getStatus());

        payment.setStatus(PaymentStatus.CANCELLED);
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
    }

    /**
     * Tests equals with same object.
     * Verifies reflexive property of equals.
     */
    @Test
    void testEquals_SameObject() {
        Payment payment = createTestPayment("PAY-001");

        assertEquals(payment, payment);
    }

    /**
     * Tests equals with equal objects.
     * Verifies payments with same ID are equal.
     */
    @Test
    void testEquals_EqualObjects() {
        Payment payment1 = createTestPayment("PAY-001");
        Payment payment2 = createTestPayment("PAY-001");

        assertEquals(payment1, payment2);
    }

    /**
     * Tests equals with different payment IDs.
     * Verifies payments with different IDs are not equal.
     */
    @Test
    void testEquals_DifferentPaymentIds() {
        Payment payment1 = createTestPayment("PAY-001");
        Payment payment2 = createTestPayment("PAY-002");

        assertNotEquals(payment1, payment2);
    }

    /**
     * Tests equals with null.
     * Verifies payment is not equal to null.
     */
    @Test
    void testEquals_Null() {
        Payment payment = createTestPayment("PAY-001");

        assertNotEquals(null, payment);
    }

    /**
     * Tests equals with different class.
     * Verifies payment is not equal to object of different class.
     */
    @Test
    void testEquals_DifferentClass() {
        Payment payment = createTestPayment("PAY-001");

        assertNotEquals("PAY-001", payment);
    }

    /**
     * Tests hashCode consistency.
     * Verifies hashCode returns same value for same object.
     */
    @Test
    void testHashCode_Consistency() {
        Payment payment = createTestPayment("PAY-001");

        int hashCode1 = payment.hashCode();
        int hashCode2 = payment.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    /**
     * Tests hashCode equality.
     * Verifies equal objects have same hashCode.
     */
    @Test
    void testHashCode_EqualObjects() {
        Payment payment1 = createTestPayment("PAY-001");
        Payment payment2 = createTestPayment("PAY-001");

        assertEquals(payment1.hashCode(), payment2.hashCode());
    }

    /**
     * Tests hashCode with different payment IDs.
     * Verifies different objects likely have different hashCodes.
     */
    @Test
    void testHashCode_DifferentPaymentIds() {
        Payment payment1 = createTestPayment("PAY-001");
        Payment payment2 = createTestPayment("PAY-002");

        assertNotEquals(payment1.hashCode(), payment2.hashCode());
    }

    /**
     * Tests equals with null paymentId.
     * Verifies equals handles null paymentId.
     */
    @Test
    void testEquals_NullPaymentId() {
        Payment payment1 = new Payment();
        Payment payment2 = new Payment();

        assertEquals(payment1, payment2);
    }

    /**
     * Tests hashCode with null paymentId.
     * Verifies hashCode handles null paymentId.
     */
    @Test
    void testHashCode_NullPaymentId() {
        Payment payment = new Payment();

        assertDoesNotThrow(() -> payment.hashCode());
    }

    /**
     * Helper method to create a test payment.
     */
    private Payment createTestPayment(String paymentId) {
        return new Payment(
                paymentId,
                "ACC-001",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.now().plusDays(5),
                null,
                PaymentStatus.SCHEDULED
        );
    }
}
