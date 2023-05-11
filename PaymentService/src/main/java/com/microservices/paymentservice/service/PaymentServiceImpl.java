package com.microservices.paymentservice.service;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.mappers.PaymentMapper;
import com.microservices.paymentservice.model.PaymentRequest;
import com.microservices.paymentservice.model.PaymentResponse;
import com.microservices.paymentservice.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public TransactionDetails doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details: {}", paymentRequest);

        TransactionDetails transactionDetails = PaymentMapper.INSTANCE.PaymentRequestToTransactionDetails(paymentRequest);

        paymentRepository.save(transactionDetails);
        log.info("Transaction complete with id: {}", transactionDetails.getId());
        return transactionDetails;
    }

    @Override
    public PaymentResponse geyPaymentByOrderId(long orderId) {
        log.info("Get payment by order id {}.", orderId);

        //TODO: handle exception (NOT_FOUND)
        TransactionDetails transactionDetails = paymentRepository.findByOrderId(orderId);

        return PaymentMapper.INSTANCE.transactionDetailsToPaymentResponse(transactionDetails);
    }
}
