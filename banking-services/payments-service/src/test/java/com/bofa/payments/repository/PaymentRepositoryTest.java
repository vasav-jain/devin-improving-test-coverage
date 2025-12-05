package com.bofa.payments.repository;

import com.bofa.payments.exception.PaymentNotFoundException;
import com.bofa.payments.model.Payment;
import com.bofa.payments.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PaymentRepository.
 * Tests cover CRUD operations, finding by account, and error handling.
 */
public class PaymentRepositoryTest {

    private PaymentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PaymentRepository();
    }

    /**
     * Tests adding a payment to the repository.
     * Verifies payment is stored and returned correctly.
     */
    @Test
    void testAdd_ValidPayment() {
        Payment payment = createTestPayment("PAY-001", "ACC-001");

        Payment result = repository.add(payment);

        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
        assertEquals(payment.getAccountId(), result.getAccountId());
    }

    /**
     * Tests creating a scheduled payment.
     * Verifies payment is created with SCHEDULED status and generated ID.
     */
    @Test
    void testCreateScheduled_ValidInputs() {
        String accountId = "ACC-001";
        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal interest = BigDecimal.valueOf(50);
        LocalDate date = LocalDate.now().plusDays(5);

        Payment result = repository.createScheduled(accountId, principal, interest, date);

        assertNotNull(result);
        assertNotNull(result.getPaymentId());
        assertEquals(accountId, result.getAccountId());
        assertEquals(principal, result.getPrincipal());
        assertEquals(interest, result.getInterest());
        assertEquals(date, result.getScheduledDate());
        assertEquals(PaymentStatus.SCHEDULED, result.getStatus());
    }

    /**
     * Tests finding an existing payment by ID.
     * Verifies correct payment is returned.
     */
    @Test
    void testFind_ExistingPayment() {
        Payment payment = createTestPayment("PAY-001", "ACC-001");
        repository.add(payment);

        Payment result = repository.find("PAY-001");

        assertNotNull(result);
        assertEquals("PAY-001", result.getPaymentId());
    }

    /**
     * Tests finding a non-existent payment throws PaymentNotFoundException.
     * Validates error handling for missing payments.
     */
    @Test
    void testFind_NonExistentPayment() {
        Exception exception = assertThrows(
            PaymentNotFoundException.class,
            () -> repository.find("NON-EXISTENT")
        );

        assertTrue(exception.getMessage().contains("Payment not found"));
        assertTrue(exception.getMessage().contains("NON-EXISTENT"));
    }

    /**
     * Tests finding payments by account ID.
     * Verifies all payments for account are returned.
     */
    @Test
    void testFindByAccount_ExistingAccount() {
        Payment payment1 = createTestPayment("PAY-001", "ACC-001");
        payment1.setScheduledDate(LocalDate.now().plusDays(5));
        repository.add(payment1);

        Payment payment2 = createTestPayment("PAY-002", "ACC-001");
        payment2.setScheduledDate(LocalDate.now().plusDays(10));
        repository.add(payment2);

        Payment payment3 = createTestPayment("PAY-003", "ACC-002");
        repository.add(payment3);

        List<Payment> results = repository.findByAccount("ACC-001");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.getAccountId().equals("ACC-001")));
    }

    /**
     * Tests finding payments by non-existent account.
     * Verifies empty list is returned.
     */
    @Test
    void testFindByAccount_NonExistentAccount() {
        List<Payment> results = repository.findByAccount("NON-EXISTENT");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    /**
     * Tests that findByAccount returns payments sorted by scheduled date.
     * Validates sorting order.
     */
    @Test
    void testFindByAccount_SortedByScheduledDate() {
        Payment payment1 = createTestPayment("PAY-001", "ACC-001");
        payment1.setScheduledDate(LocalDate.now().plusDays(10));
        repository.add(payment1);

        Payment payment2 = createTestPayment("PAY-002", "ACC-001");
        payment2.setScheduledDate(LocalDate.now().plusDays(5));
        repository.add(payment2);

        Payment payment3 = createTestPayment("PAY-003", "ACC-001");
        payment3.setScheduledDate(LocalDate.now().plusDays(15));
        repository.add(payment3);

        List<Payment> results = repository.findByAccount("ACC-001");

        assertEquals(3, results.size());
        assertTrue(results.get(0).getScheduledDate().isBefore(results.get(1).getScheduledDate()));
        assertTrue(results.get(1).getScheduledDate().isBefore(results.get(2).getScheduledDate()));
    }

    /**
     * Tests updating an existing payment.
     * Verifies changes are persisted.
     */
    @Test
    void testUpdate_ExistingPayment() {
        Payment payment = createTestPayment("PAY-001", "ACC-001");
        repository.add(payment);

        payment.setStatus(PaymentStatus.EXECUTED);
        payment.setExecutedDate(LocalDate.now());
        repository.update(payment);

        Payment result = repository.find("PAY-001");
        assertEquals(PaymentStatus.EXECUTED, result.getStatus());
        assertNotNull(result.getExecutedDate());
    }

    /**
     * Tests updating payment principal.
     * Verifies principal change is persisted.
     */
    @Test
    void testUpdate_ChangePrincipal() {
        Payment payment = createTestPayment("PAY-001", "ACC-001");
        repository.add(payment);

        BigDecimal newPrincipal = BigDecimal.valueOf(2000);
        payment.setPrincipal(newPrincipal);
        repository.update(payment);

        Payment result = repository.find("PAY-001");
        assertEquals(newPrincipal, result.getPrincipal());
    }

    /**
     * Tests updating payment interest.
     * Verifies interest change is persisted.
     */
    @Test
    void testUpdate_ChangeInterest() {
        Payment payment = createTestPayment("PAY-001", "ACC-001");
        repository.add(payment);

        BigDecimal newInterest = BigDecimal.valueOf(100);
        payment.setInterest(newInterest);
        repository.update(payment);

        Payment result = repository.find("PAY-001");
        assertEquals(newInterest, result.getInterest());
    }

    /**
     * Tests that seeded payments exist after seed() is called.
     * Validates @PostConstruct seed method by calling it manually.
     */
    @Test
    void testSeed_PaymentsExist() {
        repository.seed();
        
        List<Payment> chk100Payments = repository.findByAccount("CHK-100");
        List<Payment> chk200Payments = repository.findByAccount("CHK-200");

        assertFalse(chk100Payments.isEmpty(), "Seeded payment for CHK-100 should exist");
        assertFalse(chk200Payments.isEmpty(), "Seeded payment for CHK-200 should exist");
    }

    /**
     * Tests seeded payment for CHK-100 has correct values.
     * Validates seed data integrity.
     */
    @Test
    void testSeed_CHK100PaymentValues() {
        repository.seed();
        
        List<Payment> payments = repository.findByAccount("CHK-100");

        assertFalse(payments.isEmpty());
        Payment payment = payments.get(0);
        assertEquals(BigDecimal.valueOf(1200), payment.getPrincipal());
        assertEquals(PaymentStatus.SCHEDULED, payment.getStatus());
    }

    /**
     * Tests seeded payment for CHK-200 has correct values.
     * Validates seed data integrity.
     */
    @Test
    void testSeed_CHK200PaymentValues() {
        repository.seed();
        
        List<Payment> payments = repository.findByAccount("CHK-200");

        assertFalse(payments.isEmpty());
        Payment payment = payments.get(0);
        assertEquals(BigDecimal.valueOf(850), payment.getPrincipal());
        assertEquals(PaymentStatus.SCHEDULED, payment.getStatus());
    }

    /**
     * Tests multiple payments can be added for same account.
     * Validates repository handles multiple payments per account.
     */
    @Test
    void testAdd_MultiplePaymentsSameAccount() {
        for (int i = 0; i < 5; i++) {
            Payment payment = createTestPayment("PAY-" + i, "ACC-001");
            payment.setScheduledDate(LocalDate.now().plusDays(i + 1));
            repository.add(payment);
        }

        List<Payment> results = repository.findByAccount("ACC-001");
        assertEquals(5, results.size());
    }

    /**
     * Tests that created scheduled payment can be found.
     * Validates createScheduled and find integration.
     */
    @Test
    void testCreateScheduled_ThenFind() {
        Payment created = repository.createScheduled(
                "ACC-001",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.now().plusDays(5)
        );

        Payment found = repository.find(created.getPaymentId());

        assertEquals(created.getPaymentId(), found.getPaymentId());
        assertEquals(created.getAccountId(), found.getAccountId());
    }

    /**
     * Helper method to create a test payment.
     */
    private Payment createTestPayment(String paymentId, String accountId) {
        return new Payment(
                paymentId,
                accountId,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(50),
                LocalDate.now().plusDays(5),
                null,
                PaymentStatus.SCHEDULED
        );
    }
}
