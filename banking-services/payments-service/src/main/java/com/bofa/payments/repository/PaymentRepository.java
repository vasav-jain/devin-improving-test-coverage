package com.bofa.payments.repository;

import com.bofa.payments.exception.PaymentNotFoundException;
import com.bofa.payments.model.Payment;
import com.bofa.payments.model.PaymentStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentRepository {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        add(PaymentSeedFactory.create("CHK-100", BigDecimal.valueOf(1200), LocalDate.now().plusDays(2)));
        add(PaymentSeedFactory.create("CHK-200", BigDecimal.valueOf(850), LocalDate.now().plusDays(5)));
    }

    public Payment add(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Payment createScheduled(String accountId, BigDecimal principal, BigDecimal interest, LocalDate date) {
        Payment payment = new Payment(UUID.randomUUID().toString(), accountId, principal, interest, date, null, PaymentStatus.SCHEDULED);
        return add(payment);
    }

    public Payment find(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new PaymentNotFoundException(paymentId);
        }
        return payment;
    }

    public List<Payment> findByAccount(String accountId) {
        return payments.values().stream()
                .filter(p -> p.getAccountId().equals(accountId))
                .sorted((a, b) -> a.getScheduledDate().compareTo(b.getScheduledDate()))
                .toList();
    }

    public void update(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    private static class PaymentSeedFactory {
        static Payment create(String accountId, BigDecimal principal, LocalDate date) {
            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setAccountId(accountId);
            payment.setPrincipal(principal);
            payment.setInterest(principal.multiply(BigDecimal.valueOf(0.03)));
            payment.setScheduledDate(date);
            payment.setStatus(PaymentStatus.SCHEDULED);
            return payment;
        }
    }
}
