package com.foodtech.kitchen.infrastructure.persistence.adapters;

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
        com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper mapper = 
            new com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper();
        adapter = new OrderRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save order using JPA repository")
    void shouldSaveOrder() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        Order order = new Order("A1", List.of(cocaCola, pizza));

        com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity p1 =
            com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity.builder()
                .name("Coca Cola").type(ProductType.DRINK).build();
        com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity p2 =
            com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity.builder()
                .name("Pizza").type(ProductType.HOT_DISH).build();

        OrderEntity savedEntity = OrderEntity.builder()
            .id(1L)
            .tableNumber("A1")
            .products(List.of(p1, p2))
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
            assertNotNull(entity.getProducts());
            assertTrue(entity.getProducts().stream().anyMatch(pe -> pe.getName().equals("Coca Cola")));
            assertTrue(entity.getProducts().stream().anyMatch(pe -> pe.getType() == ProductType.DRINK));
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
            entity.getProducts().stream().anyMatch(pe -> pe.getName().equals("Coca Cola")) &&
            entity.getProducts().stream().anyMatch(pe -> pe.getName().equals("Sprite")) &&
            entity.getProducts().stream().anyMatch(pe -> pe.getName().equals("Pizza"))
        ));
    }
}