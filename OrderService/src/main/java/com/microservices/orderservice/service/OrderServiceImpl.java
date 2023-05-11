package com.microservices.orderservice.service;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.exeption.OrderServiceCustomException;
import com.microservices.orderservice.mappers.OrderMapper;
import com.microservices.orderservice.model.*;
import com.microservices.orderservice.repository.OrderRepository;
import com.microservices.orderservice.response.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
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

        log.info("Calling product service to reduce quantity.");
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

        log.info("Calling payment service to complete the payment.");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getAmount())
                .build();

        webClient.post()
                .uri("http://localhost:8083/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(paymentRequest))
                .retrieve()
                //TODO: handle exception and rollback for the reduce quantity. If the payment fails the quantity will return to the old quantity.
                /*.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))*/
                .toBodilessEntity()
                .block();

        log.info("Payment done Successfully. Changing the Oder status to PLACED");
        order.setOrderStatus("PLACED");
        orderRepository.save(order);

        log.info("Order placed successfully {}.", savedOrder);
        return order;
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order id {}.", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderServiceCustomException(
                        String.format("Order not found for id %s.", orderId),
                        "NOT_FOUND",
                        404));

        log.info("Calling to product service to fetch the product for id {}.", order.getProductId());
        ProductDetails productDetails = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8080)
                        .path("/product/{id}")
                        .build(order.getProductId()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorBody -> Mono.error(new OrderServiceCustomException(
                                errorBody.getErrorMessage(),
                                errorBody.getErrorCode(),
                                clientResponse.statusCode().value()
                        ))))
                .bodyToMono(ProductDetails.class)
                .block();

        log.info("Calling to Payment service to fetch the payment for order id {}.", order.getId());
        PaymentDetails paymentDetails = webClient.get()
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

        OrderResponse orderResponse = OrderMapper.INSTANCE.orderToOrderResponse(order);
        //TODO: check how to to make the OrderMapper.INSTANCE assign the values.
        orderResponse.setProductDetails(productDetails);
        orderResponse.setPaymentDetails(paymentDetails);
        return orderResponse;
    }
}
