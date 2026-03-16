package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByStatus(OrderStatus status);
    void deleteById(Long id);
}
