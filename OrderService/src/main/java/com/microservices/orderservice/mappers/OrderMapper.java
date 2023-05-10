package com.microservices.orderservice.mappers;

import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.model.OrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderStatus", constant = "CREATED")
    @Mapping(target = "orderDate", expression = "java(java.time.Instant.now())")
    Order orderRequestToOrder(OrderRequest orderRequest);
}
