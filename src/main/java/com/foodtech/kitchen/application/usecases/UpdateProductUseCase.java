package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.ProductNotFoundException;
import com.foodtech.kitchen.application.ports.in.UpdateProductPort;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.usecases.dto.UpdateProductCommand;
import com.foodtech.kitchen.domain.model.Product;

import java.util.UUID;

public class UpdateProductUseCase implements UpdateProductPort {

    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UUID id, UpdateProductCommand command) {
        Product existing = productRepository.findByUuid(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        Product updated = existing.update(
                command.getName(),
                command.getDescription(),
                command.getType(),
                command.getCategory(),
                command.getPrice(),
                command.getStatus()
        );

        return productRepository.save(updated);
    }
}
