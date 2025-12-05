package com.bofa.payments.service;

import com.bofa.payments.exception.PaymentValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class InterestCalculator {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_EVEN);

    public BigDecimal calculateDailyCompound(BigDecimal principal, BigDecimal annualRate, int days) {
        validate(principal, annualRate, days);
        if (days == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), MC);
        int chargeableDays = Math.max(0, days - 3); // 3-day grace period
        if (chargeableDays == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal factor = BigDecimal.ONE.add(dailyRate).pow(chargeableDays, MC);
        BigDecimal result = principal.multiply(factor.subtract(BigDecimal.ONE), MC);
        return result.setScale(2, RoundingMode.HALF_EVEN);
    }

    private void validate(BigDecimal principal, BigDecimal rate, int days) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Principal must be positive");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Annual rate must be positive");
        }
        if (days < 0 || days > 3650) {
            throw new PaymentValidationException("Days must be between 0 and 3650");
        }
    }
}
