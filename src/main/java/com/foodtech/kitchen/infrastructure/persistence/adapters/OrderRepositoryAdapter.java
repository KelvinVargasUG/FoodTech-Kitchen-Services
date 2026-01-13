package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OrderJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.OrderEntityMapper;
import org.springframework.stereotype.Component;

//HUMAN REVIEW: Simplifiqué adapter inyectando OrderEntityMapper dedicado.
//Cumple SRP: este adapter solo adapta entre JPA y dominio, no serializa.
//Mapper maneja serialización JSON, cumpliendo separación de responsabilidades.
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}