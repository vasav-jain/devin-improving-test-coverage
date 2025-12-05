package com.bofa.payments.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Payment exception classes.
 * Tests cover PaymentValidationException and PaymentNotFoundException.
 */
public class PaymentExceptionTest {

    /**
     * Tests PaymentValidationException constructor with message.
     * Verifies exception message is set correctly.
     */
    @Test
    void testPaymentValidationException_Message() {
        String message = "Principal must be positive";

        PaymentValidationException exception = new PaymentValidationException(message);

        assertEquals(message, exception.getMessage());
    }

    /**
     * Tests PaymentValidationException is a RuntimeException.
     * Verifies exception hierarchy.
     */
    @Test
    void testPaymentValidationException_IsRuntimeException() {
        PaymentValidationException exception = new PaymentValidationException("Test");

        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Tests PaymentValidationException can be thrown and caught.
     * Verifies exception can be used in try-catch.
     */
    @Test
    void testPaymentValidationException_ThrowAndCatch() {
        String message = "Invalid input";

        Exception caught = assertThrows(
            PaymentValidationException.class,
            () -> { throw new PaymentValidationException(message); }
        );

        assertEquals(message, caught.getMessage());
    }

    /**
     * Tests PaymentNotFoundException constructor with paymentId.
     * Verifies exception message includes payment ID.
     */
    @Test
    void testPaymentNotFoundException_Message() {
        String paymentId = "PAY-123";

        PaymentNotFoundException exception = new PaymentNotFoundException(paymentId);

        assertTrue(exception.getMessage().contains("Payment not found"));
        assertTrue(exception.getMessage().contains(paymentId));
    }

    /**
     * Tests PaymentNotFoundException is a RuntimeException.
     * Verifies exception hierarchy.
     */
    @Test
    void testPaymentNotFoundException_IsRuntimeException() {
        PaymentNotFoundException exception = new PaymentNotFoundException("PAY-001");

        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Tests PaymentNotFoundException can be thrown and caught.
     * Verifies exception can be used in try-catch.
     */
    @Test
    void testPaymentNotFoundException_ThrowAndCatch() {
        String paymentId = "PAY-456";

        Exception caught = assertThrows(
            PaymentNotFoundException.class,
            () -> { throw new PaymentNotFoundException(paymentId); }
        );

        assertTrue(caught.getMessage().contains(paymentId));
    }

    /**
     * Tests PaymentNotFoundException message format.
     * Verifies message follows expected format.
     */
    @Test
    void testPaymentNotFoundException_MessageFormat() {
        String paymentId = "PAY-789";

        PaymentNotFoundException exception = new PaymentNotFoundException(paymentId);

        assertEquals("Payment not found for id=" + paymentId, exception.getMessage());
    }

    /**
     * Tests PaymentValidationException with empty message.
     * Verifies empty message is handled.
     */
    @Test
    void testPaymentValidationException_EmptyMessage() {
        PaymentValidationException exception = new PaymentValidationException("");

        assertEquals("", exception.getMessage());
    }

    /**
     * Tests PaymentNotFoundException with empty paymentId.
     * Verifies empty ID is handled.
     */
    @Test
    void testPaymentNotFoundException_EmptyPaymentId() {
        PaymentNotFoundException exception = new PaymentNotFoundException("");

        assertTrue(exception.getMessage().contains("Payment not found"));
    }
}
