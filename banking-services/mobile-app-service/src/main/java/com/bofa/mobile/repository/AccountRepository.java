package com.bofa.mobile.repository;

import com.bofa.mobile.model.MobileAccount;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AccountRepository {

    private final Map<String, MobileAccount> accounts = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        MobileAccount account1 = new MobileAccount("seed-user", "Primary Checking", BigDecimal.valueOf(5400));
        accounts.put(account1.getAccountId(), account1);
    }

    public MobileAccount save(MobileAccount account) {
        accounts.put(account.getAccountId(), account);
        return account;
    }

    public List<MobileAccount> findByUserId(String userId) {
        return accounts.values().stream()
                .filter(acc -> acc.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<MobileAccount> findAll() {
        return accounts.values().stream().toList();
    }
}
