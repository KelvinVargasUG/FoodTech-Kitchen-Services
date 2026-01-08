package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OrderJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderRepositoryAdapterTest {

    private OrderRepositoryAdapter adapter;
    private OrderJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(OrderJpaRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();
        com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper mapper = 
            new com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper(objectMapper);
        adapter = new OrderRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save order using JPA repository")
    void shouldSaveOrder() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        Order order = new Order("A1", List.of(cocaCola, pizza));

        OrderEntity savedEntity = OrderEntity.builder()
            .id(1L)
            .tableNumber("A1")
            .productsJson("[{\"name\":\"Coca Cola\",\"type\":\"DRINK\"},{\"name\":\"Pizza\",\"type\":\"HOT_DISH\"}]")
            .build();

        when(jpaRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);

        // When
        adapter.save(order);

        // Then
        verify(jpaRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should convert Order to OrderEntity correctly")
    void shouldConvertOrderToEntity() {
        // Given
        Product product = new Product("Coca Cola", ProductType.DRINK);
        Order order = new Order("B2", List.of(product));

        OrderEntity capturedEntity = OrderEntity.builder().build();
        
        when(jpaRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity entity = invocation.getArgument(0);
            assertEquals("B2", entity.getTableNumber());
            assertNotNull(entity.getProductsJson());
            assertTrue(entity.getProductsJson().contains("Coca Cola"));
            assertTrue(entity.getProductsJson().contains("DRINK"));
            return entity;
        });

        // When
        adapter.save(order);

        // Then
        verify(jpaRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should handle order with multiple products")
    void shouldHandleMultipleProducts() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product sprite = new Product("Sprite", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        Order order = new Order("C3", List.of(cocaCola, sprite, pizza));

        // When
        adapter.save(order);

        // Then
        verify(jpaRepository, times(1)).save(argThat(entity -> 
            entity.getTableNumber().equals("C3") &&
            entity.getProductsJson().contains("Coca Cola") &&
            entity.getProductsJson().contains("Sprite") &&
            entity.getProductsJson().contains("Pizza")
        ));
    }
}