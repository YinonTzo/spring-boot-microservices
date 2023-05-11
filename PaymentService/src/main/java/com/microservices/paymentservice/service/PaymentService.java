package com.microservices.paymentservice.service;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.model.PaymentRequest;
import com.microservices.paymentservice.model.PaymentResponse;

public interface PaymentService {
    TransactionDetails doPayment(PaymentRequest paymentRequest);

    PaymentResponse geyPaymentByOrderId(long orderId);
}
