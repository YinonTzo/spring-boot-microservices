package com.microservices.productservice.service;

import com.microservices.productservice.entity.Product;
import com.microservices.productservice.model.ProductRequest;
import com.microservices.productservice.model.ProductResponse;

public interface ProductService {
    Product addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long id);

    void reduceQuantity(long productId, long quantity);
}
