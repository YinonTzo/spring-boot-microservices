package com.microservices.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.model.PaymentRequest;
import com.microservices.paymentservice.model.PaymentResponse;
import com.microservices.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void doPayment() throws Exception {
        // Arrange
        long orderId = 1;

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .build();

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .orderId(orderId)
                .build();

        when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(transactionDetails);

        // Act
        MvcResult result = mockMvc.perform(post("/payment", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        TransactionDetails responseTransactionDetails = objectMapper.readValue(responseContent, TransactionDetails.class);

        assertEquals(transactionDetails, responseTransactionDetails);
        verify(paymentService, times(1)).doPayment(paymentRequest);
    }

    @Test
    void getPaymentByOrderId() throws Exception {
        // Arrange
        long orderId = 1;

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .orderId(orderId)
                .paymentStatus("Payment Status")
                .build();

        when(paymentService.geyPaymentByOrderId(orderId)).thenReturn(paymentResponse);

        // Act
        MvcResult result = mockMvc.perform(get("/payment/order/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        PaymentResponse responsePayment = objectMapper.readValue(responseContent, PaymentResponse.class);

        assertEquals(paymentResponse, responsePayment);
        verify(paymentService, times(1)).geyPaymentByOrderId(orderId);
    }
}