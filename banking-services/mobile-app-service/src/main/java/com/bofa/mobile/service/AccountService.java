package com.bofa.mobile.service;

import com.bofa.mobile.dto.AccountSummaryResponse;
import com.bofa.mobile.model.MobileAccount;
import com.bofa.mobile.model.MobileTransaction;
import com.bofa.mobile.repository.AccountRepository;
import com.bofa.mobile.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<MobileAccount> getUserAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<MobileTransaction> getTransactionsForAccount(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public AccountSummaryResponse calculateSpendingSummary(String userId) {
        List<MobileAccount> accounts = accountRepository.findByUserId(userId);
        BigDecimal totalBalance = accounts.stream()
                .map(MobileAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MobileTransaction> transactions = accounts.stream()
                .flatMap(acc -> transactionRepository.findByAccountId(acc.getAccountId()).stream())
                .toList();

        BigDecimal averageDebit = average(transactions.stream()
                .filter(tx -> tx.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(tx -> tx.getAmount().abs())
                .toList());
        BigDecimal averageCredit = average(transactions.stream()
                .filter(tx -> tx.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(MobileTransaction::getAmount)
                .toList());

        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(MobileTransaction::getCategory,
                        Collectors.mapping(MobileTransaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        return new AccountSummaryResponse(userId, totalBalance, averageDebit, averageCredit, categoryTotals);
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_EVEN);
    }
}
