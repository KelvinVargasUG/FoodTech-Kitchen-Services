package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    boolean existsByName(String name);
    List<Product> findAllActive();
    List<Product> findAllActiveByCategory(String category);
    List<Product> findAllActiveByNameContaining(String name);
    Optional<Product> findByUuid(UUID uuid);
    Optional<Product> findByName(String name);
    void updateStatus(UUID uuid, ProductStatus status);
}
