package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

//HUMAN REVIEW: Extraje la lógica de serialización JSON a mapper dedicado.
//Cumple SRP: OrderRepositoryAdapter solo adapta JPA, este mapper solo serializa.
//Elimina duplicación: ProductDto centralizado, reutilizable por otros mappers.
@Component
public class OrderEntityMapper {

    private final ObjectMapper objectMapper;

    public OrderEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderEntity toEntity(Order order) {
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

    public record ProductDto(String name, String type) {}
}
