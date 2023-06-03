package com.microservices.paymentservice.service;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.exeption.PaymentServiceCustomException;
import com.microservices.paymentservice.model.PaymentMode;
import com.microservices.paymentservice.model.PaymentRequest;
import com.microservices.paymentservice.model.PaymentResponse;
import com.microservices.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;

    private TransactionDetails transactionDetails;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .orderId(123)
                .paymentMode(PaymentMode.CREDIT_CARD)
                .build();

        transactionDetails = TransactionDetails.builder()
                .orderId(123)
                .paymentMode(PaymentMode.CREDIT_CARD)
                .paymentDate(Instant.now())
                .build();
    }

    @Test
    void doPayment() {
        // Arrange
        when(paymentRepository.save(any(TransactionDetails.class))).thenReturn(transactionDetails);

        // Act
        TransactionDetails result = paymentService.doPayment(paymentRequest);

        // Assert
        assertEquals(transactionDetails.getOrderId(), result.getOrderId());
        verify(paymentRepository, times(1)).save(any(TransactionDetails.class));
    }

    @Test
    void geyPaymentByOrderId() {
        // Arrange
        when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.of(transactionDetails));

        // Act
        PaymentResponse result = paymentService.geyPaymentByOrderId(123);

        // Assert
        assertEquals(transactionDetails.getId(), result.getId());
        assertEquals(transactionDetails.getOrderId(), result.getOrderId());
        assertEquals(transactionDetails.getPaymentMode(), result.getPaymentMode());
        assertEquals(transactionDetails.getPaymentStatus(), result.getPaymentStatus());
        assertEquals(transactionDetails.getPaymentDate(), result.getPaymentDate());
        verify(paymentRepository, times(1)).findByOrderId(anyLong());
    }

    @Test
    public void testGetPaymentByOrderId_notFound() {
        // Arrange
        when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentServiceCustomException.class, () -> paymentService.geyPaymentByOrderId(123));
        verify(paymentRepository, times(1)).findByOrderId(anyLong());
    }
}