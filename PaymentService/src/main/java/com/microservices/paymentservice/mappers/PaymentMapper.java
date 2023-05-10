package com.microservices.paymentservice.mappers;

import com.microservices.paymentservice.entity.TransactionDetails;
import com.microservices.paymentservice.model.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentStatus", constant = "SUCCESS")
    @Mapping(target = "paymentDate", expression = "java(java.time.Instant.now())")
    TransactionDetails PaymentRequestToTransactionDetails(PaymentRequest paymentRequest);
}