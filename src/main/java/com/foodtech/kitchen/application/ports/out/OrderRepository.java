package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
