package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.exeption.OrderServiceCustomException;
import com.microservices.orderservice.mappers.OrderMapper;
import com.microservices.orderservice.model.*;
import com.microservices.orderservice.repository.OrderRepository;
import com.microservices.orderservice.response.ErrorResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

//TODO: check how to implement the web client correctly. Maybe create some caller object.
@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final WebClient webClient;

    @Override
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public Order placeOrder(OrderRequest orderRequest) {
        log.info("Placing order request {}.", orderRequest);

        log.info("Calling the product service to reduce quantity.");
        reduceQuantityInProductService(orderRequest);

        log.info("Creating order with status CREATED.");
        Order order = OrderMapper.INSTANCE.orderRequestToOrder(orderRequest);

        Order savedOrder = orderRepository.save(order);

        log.info("Calling the payment service to complete the payment.");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getAmount())
                .build();

        payInPaymentService(paymentRequest);
        //TODO: handle rollback for the reduce quantity. If the payment fails the quantity will return to the old quantity.

        log.info("Payment done Successfully. Changing the Oder status to PLACED");
        order.setOrderStatus("PLACED");
        orderRepository.save(order);

        log.info("Order placed successfully {}.", savedOrder);
        return order;
    }

    private void payInPaymentService(PaymentRequest paymentRequest) {
        webClient.post()
                .uri("http://localhost:8083/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(paymentRequest))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))
                .toBodilessEntity()
                .block();
    }

    private void reduceQuantityInProductService(OrderRequest orderRequest) {
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
    }

    @Override
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order id {}.", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderServiceCustomException(
                        String.format("Order not found for id %s.", orderId),
                        "NOT_FOUND",
                        404));

        log.info("Calling the product service to fetch the product id {}.", order.getProductId());
        ProductDetails productDetails = getProductDetails(order);

        log.info("Calling the Payment service to fetch the payment order id {}.", order.getId());
        PaymentDetails paymentDetails = getPaymentDetails(order);

        OrderResponse orderResponse = OrderMapper.INSTANCE.orderToOrderResponse(order);
        //TODO: check how to to make the OrderMapper.INSTANCE assign the values.
        orderResponse.setProductDetails(productDetails);
        orderResponse.setPaymentDetails(paymentDetails);
        return orderResponse;
    }

    private PaymentDetails getPaymentDetails(Order order) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8083)
                        .path("/payment/order/{id}")
                        .build(order.getId()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))
                .bodyToMono(PaymentDetails.class)
                .block();
    }

    private ProductDetails getProductDetails(Order order) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8080)
                        .path("/product/{id}")
                        .build(order.getProductId()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))
                .bodyToMono(ProductDetails.class)
                .block();
    }

    private OrderResponse fallback(long orderId, WebClientRequestException ex) {
        log.info(ex.getMessage());

        throw new OrderServiceCustomException(
                "Some service isn't accessible.",
                "UNAVAILABLE",
                500
        );
    }
}