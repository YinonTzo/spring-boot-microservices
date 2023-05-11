package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.model.OrderRequest;
import com.microservices.orderservice.model.OrderResponse;

public interface OrderService {
    Order placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
