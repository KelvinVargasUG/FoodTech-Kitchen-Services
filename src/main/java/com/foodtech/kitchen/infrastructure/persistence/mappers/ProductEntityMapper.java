package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductEntityMapper {

    public ProductEntity toProductEntity(Product product) {
        return ProductEntity.builder()
                .uuid(product.getId() != null ? product.getId().toString() : null)
                .name(product.getName())
                .type(product.getType())
                .price(product.getPrice())
                .status(product.getStatus())
                .category(product.getCategory())
                .description(product.getDescription())
                .build();
    }

    public ProductEntity toProductEntity(Product product, Long jpaId) {
        return ProductEntity.builder()
                .id(jpaId)
                .uuid(product.getId() != null ? product.getId().toString() : null)
                .name(product.getName())
                .type(product.getType())
                .price(product.getPrice())
                .status(product.getStatus())
                .category(product.getCategory())
                .description(product.getDescription())
                .build();
    }

    public TaskProductEntity toTaskProductEntity(Product product) {
        return TaskProductEntity.builder()
                .name(product.getName())
                .type(product.getType())
                .build();
    }

    public OrderProductEntity toOrderProductEntity(Product product) {
        return OrderProductEntity.builder()
                .name(product.getName())
                .type(product.getType())
                .price(product.getPrice())
                .build();
    }

    public Product toDomain(OrderProductEntity entity) {
        return new Product(entity.getName(), entity.getType(), entity.getPrice());
    }

    public Product toDomain(ProductEntity entity) {
        if (entity.getUuid() != null && entity.getStatus() != null && entity.getCategory() != null) {
            return Product.reconstruct(
                    UUID.fromString(entity.getUuid()),
                    entity.getName(),
                    entity.getType(),
                    entity.getCategory(),
                    entity.getPrice(),
                    entity.getStatus(),
                    entity.getDescription()
            );
        }
        return new Product(entity.getName(), entity.getType(), entity.getPrice());
    }

    public Product toDomain(TaskProductEntity entity) {
        return new Product(entity.getName(), entity.getType(), 0);
    }
}
