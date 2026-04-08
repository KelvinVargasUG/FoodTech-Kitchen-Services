package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.application.usecases.dto.CreateProductCommand;
import com.foodtech.kitchen.domain.model.Product;

public interface CreateProductPort {
    Product execute(CreateProductCommand command);
}
