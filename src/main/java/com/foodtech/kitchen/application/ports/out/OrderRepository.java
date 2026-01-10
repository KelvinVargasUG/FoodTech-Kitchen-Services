package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;

public interface OrderRepository {
    OrderEntity save(Order order);
}
