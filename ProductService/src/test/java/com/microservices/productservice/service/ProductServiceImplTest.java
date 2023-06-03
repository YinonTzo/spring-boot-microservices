package com.microservices.productservice.service;

import com.microservices.productservice.entity.Product;
import com.microservices.productservice.exeption.ProductServiceCustomException;
import com.microservices.productservice.model.ProductRequest;
import com.microservices.productservice.model.ProductResponse;
import com.microservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .productName("Test Product")
                .price(100)
                .quantity(10)
                .build();

        product = Product.builder()
                .id(1)
                .productName("Test Product")
                .price(100)
                .quantity(10)
                .build();
    }

    @Test
    void addProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.addProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getQuantity(), result.getQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_found() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(product));

        // Act
        ProductResponse result = productService.getProductById(1);

        // Assert
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getQuantity(), result.getQuantity());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    void getProductById_Notfound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ProductServiceCustomException.class, () -> productService.getProductById(1));
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    void reduceQuantity_sufficientQuantity() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(product));

        long expectedQuantity = product.getQuantity() - 5;

        // Act
        assertDoesNotThrow(() -> productService.reduceQuantity(1, 5));

        // Assert
        assertEquals(expectedQuantity, product.getQuantity());
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void reduceQuantity_insufficientQuantity() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(product));

        // Act & Assert
        assertThrows(ProductServiceCustomException.class, () -> productService.reduceQuantity(1, 15));
        verify(productRepository, times(1)).findById(anyLong());
    }
}