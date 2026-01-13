package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public ProductEntity toProductEntity(Product product) {
        return ProductEntity.builder()
                .name(product.getName())
                .type(product.getType())
                .build();
    }

    public TaskProductEntity toTaskProductEntity(Product product) {
        return TaskProductEntity.builder()
                .name(product.getName())
                .type(product.getType())
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        return new Product(entity.getName(), entity.getType());
    }

    public Product toDomain(TaskProductEntity entity) {
        return new Product(entity.getName(), entity.getType());
    }
}
