package com.bofa.mobile.controller;

import com.bofa.mobile.dto.*;
import com.bofa.mobile.model.MobileAccount;
import com.bofa.mobile.model.MobileTransaction;
import com.bofa.mobile.service.AccountService;
import com.bofa.mobile.service.AuthService;
import com.bofa.mobile.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class MobileController {

    private final AuthService authService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public MobileController(AuthService authService,
                            AccountService accountService,
                            TransactionService transactionService) {
        this.authService = authService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping("/auth/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/auth/login")
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }

    @GetMapping("/accounts/{userId}")
    public List<MobileAccount> getUserAccounts(@PathVariable String userId) {
        return accountService.getUserAccounts(userId);
    }

    @GetMapping("/accounts/{userId}/summary")
    public AccountSummaryResponse getAccountSummary(@PathVariable String userId) {
        return accountService.calculateSpendingSummary(userId);
    }

    @GetMapping("/transactions/{accountId}")
    public List<MobileTransaction> getTransactionsForAccount(@PathVariable String accountId,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                              @RequestParam(required = false) String category,
                                                              @RequestParam(required = false) BigDecimal minAmount,
                                                              @RequestParam(required = false) BigDecimal maxAmount) {
        List<MobileTransaction> transactions = accountService.getTransactionsForAccount(accountId);
        TransactionFilter filter = new TransactionFilter();
        filter.setStartDate(start);
        filter.setEndDate(end);
        filter.setCategory(category);
        filter.setMinAmount(minAmount);
        filter.setMaxAmount(maxAmount);
        return transactionService.applyFilter(transactions, filter);
    }
}
