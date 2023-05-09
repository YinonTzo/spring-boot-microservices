package com.microservices.productservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

    private long id;
    private String productName;
    private long price;
    private long quantity;
}
