package com.microservices.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "TRANSACTION_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_sequence", allocationSize = 1)
    private long id;

    @Column(name = "ORDER_ID")
    private long orderId;

    @Column(name = "PAYMENT_ID")
    private String paymentMode;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "PAYMENT_NUMBER")
    private String paymentStatus;

    @Column(name = "PAYMENT_DATE")
    private Instant paymentDate;

    @Column(name = "AMOUNT")
    private long amount;
}
