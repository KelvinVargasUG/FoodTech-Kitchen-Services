package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OrderJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

//HUMAN REVIEW: Inyecto ObjectMapper por constructor en lugar de crear instancia propia.
//Cumple DIP: dependo de abstracción inyectada, no creo dependencia concreta.
//Beneficios: configurable, mockeable en tests, única instancia singleton compartida.
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    public OrderEntity save(Order order) {
        OrderEntity entity = toEntity(order);
        return jpaRepository.save(entity);
    }

    private OrderEntity toEntity(Order order) {
        try {
            String productsJson = objectMapper.writeValueAsString(
                order.getProducts().stream()
                    .map(p -> new ProductDto(p.getName(), p.getType().name()))
                    .collect(Collectors.toList())
            );

            return OrderEntity.builder()
                .tableNumber(order.getTableNumber())
                .productsJson(productsJson)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting order to entity", e);
        }
    }

    private record ProductDto(String name, String type) {}
}