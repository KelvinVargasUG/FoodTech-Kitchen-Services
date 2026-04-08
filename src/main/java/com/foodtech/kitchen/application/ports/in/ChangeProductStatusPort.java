package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.Product;

import java.util.UUID;

public interface ChangeProductStatusPort {
    Product deactivate(UUID productId);
    Product activate(UUID productId);
}
