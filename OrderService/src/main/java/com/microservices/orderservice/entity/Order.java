package com.microservices.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "ORDER_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    private long id;

    @Column(name = "PRODUCT_ID")
    private long productId;

    @Column(name = "QUANTITY")
    private long quantity;

    @Column(name = "TOTAL_AMOUNT")
    private long amount;

    @Column(name = "ORDER_DATE")
    private Instant orderDate;

    @Column(name = "ORDER_STATUS")
    private String orderStatus;
}
