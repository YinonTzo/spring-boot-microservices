package com.microservices.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO: validation when assigment the order (product id can't be empty).
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetails {

    private long id;
    private String productName;
    private long price;
    private long quantity;
}
