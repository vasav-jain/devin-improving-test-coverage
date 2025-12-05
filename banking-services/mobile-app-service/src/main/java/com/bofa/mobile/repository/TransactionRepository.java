package com.bofa.mobile.repository;

import com.bofa.mobile.model.MobileTransaction;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {

    private final List<MobileTransaction> transactions = new CopyOnWriteArrayList<>();

    @PostConstruct
    void seed() {
        transactions.add(new MobileTransaction("seed-account", BigDecimal.valueOf(-56.23), "Dining", LocalDate.now().minusDays(1)));
        transactions.add(new MobileTransaction("seed-account", BigDecimal.valueOf(-120.12), "Groceries", LocalDate.now().minusDays(3)));
        transactions.add(new MobileTransaction("seed-account", BigDecimal.valueOf(1200), "Payroll", LocalDate.now().minusDays(14)));
    }

    public void add(MobileTransaction transaction) {
        transactions.add(transaction);
    }

    public List<MobileTransaction> findByAccountId(String accountId) {
        return transactions.stream()
                .filter(tx -> tx.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }
}
