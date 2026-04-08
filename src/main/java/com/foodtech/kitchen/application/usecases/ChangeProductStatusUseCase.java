package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.ProductNotFoundException;
import com.foodtech.kitchen.application.ports.in.ChangeProductStatusPort;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.domain.model.Product;

import java.util.UUID;

public class ChangeProductStatusUseCase implements ChangeProductStatusPort {

    private final ProductRepository productRepository;

    public ChangeProductStatusUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product deactivate(UUID productId) {
        Product product = productRepository.findByUuid(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));
        Product deactivated = product.deactivate();
        productRepository.updateStatus(productId, deactivated.getStatus());
        return deactivated;
    }

    @Override
    public Product activate(UUID productId) {
        Product product = productRepository.findByUuid(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));
        Product activated = product.activate();
        productRepository.updateStatus(productId, activated.getStatus());
        return activated;
    }
}
