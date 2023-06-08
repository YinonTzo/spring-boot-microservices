package com.microservices.paymentservice.service;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.exeption.PaymentServiceCustomException;
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

    public static final String THERE_IS_NO_ORDER = "There is no order with id %s.";
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";

    private final PaymentRepository paymentRepository;

    @Override
    public TransactionDetails doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details: {}", paymentRequest);

        TransactionDetails transactionDetails = PaymentMapper.INSTANCE.paymentRequestToTransactionDetails(paymentRequest);

        paymentRepository.save(transactionDetails);
        log.info("Transaction complete with id: {}", transactionDetails.getId());
        return transactionDetails;
    }

    @Override
    public PaymentResponse geyPaymentByOrderId(long orderId) {
        log.info("Get payment by order id {}.", orderId);

        TransactionDetails transactionDetails = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentServiceCustomException(
                        String.format(THERE_IS_NO_ORDER, orderId),
                        ORDER_NOT_FOUND
                ));

        return PaymentMapper.INSTANCE.transactionDetailsToPaymentResponse(transactionDetails);
    }
}
