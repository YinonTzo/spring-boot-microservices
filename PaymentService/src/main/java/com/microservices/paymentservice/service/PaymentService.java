package com.microservices.paymentservice.service;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.model.PaymentRequest;

public interface PaymentService {
    TransactionDetails doPayment(PaymentRequest paymentRequest);
}
