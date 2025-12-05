package com.bofa.payments.util;

import com.bofa.payments.dto.PaymentResponse;
import com.bofa.payments.model.Payment;

public final class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getAccountId(),
                payment.getPrincipal(),
                payment.getInterest(),
                payment.getScheduledDate(),
                payment.getExecutedDate(),
                payment.getStatus()
        );
    }
}
