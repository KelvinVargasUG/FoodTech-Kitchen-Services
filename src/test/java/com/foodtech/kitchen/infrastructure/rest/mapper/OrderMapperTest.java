package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.ProductRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("component")
class OrderMapperTest {

    @Test
    @DisplayName("Should map CreateOrderRequest to Order domain")
    void shouldMapRequestToOrder() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "A1",
            "Cliente Test",
            "test@test.com",
            List.of(
                new ProductRequest("Coca Cola", "DRINK", 3),
                new ProductRequest("Pizza", "HOT_DISH", 10)
            )
        );

        // When
        Order order = OrderMapper.toDomain(request);

        // Then
        assertEquals("A1", order.getTableNumber());
        assertEquals("Cliente Test", order.getCustomerName());
        assertEquals("test@test.com", order.getCustomerEmail());
        assertEquals(2, order.getProducts().size());
        assertEquals("Coca Cola", order.getProducts().get(0).getName());
        assertEquals(ProductType.DRINK, order.getProducts().get(0).getType());
        assertEquals(3, order.getProducts().get(0).getPrice());
    }

    @Test
    @DisplayName("Should handle single product")
    void shouldHandleSingleProduct() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "B2",
            "Cliente Test",
            "test@test.com",
            List.of(
                new ProductRequest("Sprite", "DRINK", 2)
            )
        );

        // When
        Order order = OrderMapper.toDomain(request);

        // Then
        assertEquals(1, order.getProducts().size());
        assertEquals("Sprite", order.getProducts().get(0).getName());
        assertEquals(2, order.getProducts().get(0).getPrice());
    }

    @Test
    @DisplayName("Should throw exception for invalid product type")
    void shouldThrowExceptionForInvalidProductType() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            "C3",
            "Cliente Test",
            "test@test.com",
            List.of(
                new ProductRequest("Invalid", "INVALID_TYPE", 0)
            )
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> OrderMapper.toDomain(request));
    }
}