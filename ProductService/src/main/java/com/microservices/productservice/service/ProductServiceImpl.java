package com.microservices.productservice.service;

import com.microservices.productservice.entity.Product;
import com.microservices.productservice.exeption.ProductServiceCustomException;
import com.microservices.productservice.mappers.ProductMapper;
import com.microservices.productservice.model.ProductRequest;
import com.microservices.productservice.model.ProductResponse;
import com.microservices.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    public static final String THERE_IS_NO_PRODUCT = "There is no product with id %s.";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    private final ProductRepository productRepository;

    @Override
    public Product addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");

        Product product = ProductMapper.INSTANCE.productRequestToProduct(productRequest);
        productRepository.save(product);

        log.info("Product created: " + product);

        return product;
    }

    @Override
    public ProductResponse getProductById(long id) {
        log.info("Get product {}.", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductServiceCustomException(String.format(THERE_IS_NO_PRODUCT, id), PRODUCT_NOT_FOUND));

        return ProductMapper.INSTANCE.productToProductResponse(product);
    }
}
