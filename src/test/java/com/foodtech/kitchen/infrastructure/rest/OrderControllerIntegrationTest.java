package com.foodtech.kitchen.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create order and return 201 with task count")
    void shouldCreateOrderAndReturn201() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
            .tableNumber("A1")
            .products(List.of(
                ProductRequest.builder()
                    .name("Coca Cola")
                    .type("DRINK")
                    .build()
            ))
            .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableNumber").value("A1"))
                .andExpect(jsonPath("$.tasksCreated").value(1))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should create order with mixed products and return correct task count")
    void shouldCreateOrderWithMixedProducts() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
            .tableNumber("B2")
            .products(List.of(
                ProductRequest.builder()
                    .name("Coca Cola")
                    .type("DRINK")
                    .build(),
                ProductRequest.builder()
                    .name("Pizza Margarita")
                    .type("HOT_DISH")
                    .build()
            ))
            .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableNumber").value("B2"))
                .andExpect(jsonPath("$.tasksCreated").value(2));
    }

    @Test
    @DisplayName("Should reject order without products and return 400")
    void shouldRejectOrderWithoutProducts() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
            .tableNumber("C3")
            .products(List.of())
            .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Order must contain at least one product"));
    }

    @Test
    @DisplayName("Should reject order without table number and return 400")
    void shouldRejectOrderWithoutTableNumber() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
            .tableNumber(null)
            .products(List.of(
                ProductRequest.builder()
                    .name("Coca Cola")
                    .type("DRINK")
                    .build()
            ))
            .build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Table number cannot be null or empty"));
    }

    @Test
    @DisplayName("Should reject order with invalid product type and return 400")
    void shouldRejectOrderWithInvalidProductType() throws Exception {
        // Given
        String invalidRequest = """
            {
                "tableNumber": "D4",
                "products": [
                    {
                        "name": "Invalid Product",
                        "type": "INVALID_TYPE"
                    }
                ]
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}