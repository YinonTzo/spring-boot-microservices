package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.mappers.OrderMapper;
import com.microservices.orderservice.model.OrderRequest;
import com.microservices.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order placeOrder(OrderRequest orderRequest) {

        log.info("Placing order request {}", orderRequest);

        Order order = OrderMapper.INSTANCE.orderRequestToOrder(orderRequest);

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully {}.", savedOrder);
        return order;
    }
}
