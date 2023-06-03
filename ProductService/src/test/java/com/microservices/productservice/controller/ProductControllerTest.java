package com.microservices.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.entity.Product;
import com.microservices.productservice.model.ProductRequest;
import com.microservices.productservice.model.ProductResponse;
import com.microservices.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    public void addProduct() throws Exception {
        // Arrange
        ProductRequest productRequest = ProductRequest.builder()
                .productName("Test Product")
                .price(10)
                .quantity(5)
                .build();

        Product savedProduct = Product.builder()
                .id(1)
                .productName("Test Product")
                .price(10)
                .quantity(5)
                .build();

        when(productService.addProduct(any(ProductRequest.class))).thenReturn(savedProduct);

        // Act
        MvcResult result = mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        Product responseProduct = objectMapper.readValue(responseContent, Product.class);

        assertEquals(savedProduct, responseProduct);
        verify(productService, times(1)).addProduct(productRequest);
    }

    @Test
    public void getProductById() throws Exception {
        // Arrange
        long productId = 1;

        ProductResponse productResponse = ProductResponse.builder()
                .id(productId)
                .productName("Test Product")
                .price(10)
                .quantity(5)
                .build();

        when(productService.getProductById(productId)).thenReturn(productResponse);

        // Act
        MvcResult result = mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        ProductResponse responseProduct = objectMapper.readValue(responseContent, ProductResponse.class);

        assertEquals(productResponse, responseProduct);
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    public void reduceQuantity() throws Exception {
        // Arrange
        long productId = 1;
        long quantity = 5;

        // Act
        MvcResult result = mockMvc.perform(put("/product/reduceQuantity/{id}", productId)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertEquals("", result.getResponse().getContentAsString());
        verify(productService, times(1)).reduceQuantity(productId, quantity);
    }
}