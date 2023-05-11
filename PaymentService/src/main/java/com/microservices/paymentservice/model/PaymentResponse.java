package com.microservices.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private long id;
    private long orderId;
    private PaymentMode paymentMode;
    private String paymentStatus;
    private Instant paymentDate;
    private long amount;
}
