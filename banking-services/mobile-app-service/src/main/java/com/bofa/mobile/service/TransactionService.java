package com.bofa.mobile.service;

import com.bofa.mobile.dto.TransactionFilter;
import com.bofa.mobile.model.MobileTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TransactionService {

    public List<MobileTransaction> applyFilter(List<MobileTransaction> transactions, TransactionFilter filter) {
        Stream<MobileTransaction> stream = transactions.stream();
        if (filter.getStartDate().isPresent()) {
            stream = stream.filter(tx -> !tx.getDate().isBefore(filter.getStartDate().get()));
        }
        if (filter.getEndDate().isPresent()) {
            stream = stream.filter(tx -> !tx.getDate().isAfter(filter.getEndDate().get()));
        }
        if (filter.getCategory().isPresent()) {
            stream = stream.filter(tx -> tx.getCategory().equalsIgnoreCase(filter.getCategory().get()))
                    .filter(tx -> !tx.getCategory().equalsIgnoreCase("internal"));
        }
        if (filter.getMinAmount().isPresent()) {
            BigDecimal min = filter.getMinAmount().get();
            stream = stream.filter(tx -> tx.getAmount().abs().compareTo(min) >= 0);
        }
        if (filter.getMaxAmount().isPresent()) {
            BigDecimal max = filter.getMaxAmount().get();
            stream = stream.filter(tx -> tx.getAmount().abs().compareTo(max) <= 0);
        }
        return stream.toList();
    }
}
