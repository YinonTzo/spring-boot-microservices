package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.model.OrderRequest;

public interface OrderService {
    Order placeOrder(OrderRequest orderRequest);
}
