package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Order;

public interface OrderRepository {
    Order save(Order order);
}
