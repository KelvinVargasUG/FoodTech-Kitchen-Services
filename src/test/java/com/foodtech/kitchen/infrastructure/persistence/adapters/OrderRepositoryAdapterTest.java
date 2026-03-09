package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OrderJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderEntityMapper mapper;

    @InjectMocks
    private OrderRepositoryAdapter adapter;

    @Test
    void save_mapsToEntityAndBack() {
        // Arrange
        Order order = Order.reconstruct(1L, "A1", sampleProducts(), OrderStatus.CREATED);
        OrderEntity entity = OrderEntity.builder().id(1L).tableNumber("A1").build();
        OrderEntity savedEntity = OrderEntity.builder().id(1L).tableNumber("A1").build();
        Order savedOrder = Order.reconstruct(1L, "A1", sampleProducts(), OrderStatus.CREATED);

        when(mapper.toEntity(order)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        // Act
        Order result = adapter.save(order);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(mapper).toEntity(order);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void findById_mapsEntityToDomain() {
        // Arrange
        OrderEntity entity = OrderEntity.builder().id(2L).tableNumber("B2").build();
        Order order = Order.reconstruct(2L, "B2", sampleProducts(), OrderStatus.CREATED);
        when(jpaRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        // Act
        Optional<Order> result = adapter.findById(2L);

        // Assert
        assertEquals(true, result.isPresent());
        assertEquals(2L, result.get().getId());
        verify(mapper).toDomain(entity);
    }

    @Test
    void findByStatus_mapsEntityListToDomainList() {
        // Arrange
        OrderEntity entity = OrderEntity.builder().id(3L).tableNumber("C3").build();
        Order order = Order.reconstruct(3L, "C3", sampleProducts(), OrderStatus.COMPLETED);
        when(jpaRepository.findByStatus(OrderStatus.COMPLETED)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        // Act
        List<Order> result = adapter.findByStatus(OrderStatus.COMPLETED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        verify(mapper).toDomain(entity);
    }

    private List<Product> sampleProducts() {
        return List.of(new Product("Tea", ProductType.DRINK));
    }
}