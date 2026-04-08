package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.GetActiveProductsPort;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.domain.model.Product;

import java.util.List;

public class GetActiveProductsUseCase implements GetActiveProductsPort {

    private final ProductRepository productRepository;

    public GetActiveProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> execute() {
        return productRepository.findAllActive();
    }

    @Override
    public List<Product> executeByCategory(String category) {
        return productRepository.findAllActiveByCategory(category);
    }

    @Override
    public List<Product> executeByName(String name) {
        return productRepository.findAllActiveByNameContaining(name);
    }
}
