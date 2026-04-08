package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.DuplicateProductException;
import com.foodtech.kitchen.application.ports.in.CreateProductPort;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.usecases.dto.CreateProductCommand;
import com.foodtech.kitchen.domain.model.Product;

public class CreateProductUseCase implements CreateProductPort {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(CreateProductCommand command) {
        if (productRepository.existsByName(command.getName())) {
            throw new DuplicateProductException(command.getName());
        }

        Product product = Product.create(
                command.getName(),
                command.getType(),
                command.getCategory(),
                command.getPrice()
        );

        return productRepository.save(product);
    }
}
