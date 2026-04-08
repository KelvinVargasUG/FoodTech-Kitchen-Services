package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.application.usecases.dto.UpdateProductCommand;
import com.foodtech.kitchen.domain.model.Product;

import java.util.UUID;

public interface UpdateProductPort {
    Product execute(UUID id, UpdateProductCommand command);
}
