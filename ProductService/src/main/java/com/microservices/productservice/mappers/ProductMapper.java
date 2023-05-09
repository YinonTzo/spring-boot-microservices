package com.microservices.productservice.mappers;

import com.microservices.productservice.entity.Product;
import com.microservices.productservice.model.ProductRequest;
import com.microservices.productservice.model.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductResponse productToProductResponse(Product product);

    Product productRequestToProduct(ProductRequest productRequest);
}
