package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.exeption.OrderServiceCustomException;
import com.microservices.orderservice.mappers.OrderMapper;
import com.microservices.orderservice.model.OrderRequest;
import com.microservices.orderservice.repository.OrderRepository;
import com.microservices.orderservice.response.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    @Override
    public Order placeOrder(OrderRequest orderRequest) {

        log.info("Placing order request {}.", orderRequest);

        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8080)
                        .path("/product/reduceQuantity/{id}")
                        .queryParam("quantity", orderRequest.getQuantity())
                        .build(orderRequest.getProductId()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))
                .toBodilessEntity()
                .block();


        log.info("Creating order with status CREATED.");
        Order order = OrderMapper.INSTANCE.orderRequestToOrder(orderRequest);

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully {}.", savedOrder);
        return order;
    }
}
