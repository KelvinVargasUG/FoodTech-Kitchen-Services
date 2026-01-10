package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// ✅ Cumple SRP: Solo mapea entre dominio y entidades JPA
// ✅ Elimina complejidad: No más ObjectMapper, try-catch, JSON, DTOs
@Component
public class TaskEntityMapper {

    // ✅ NO más ObjectMapper - JPA maneja las relaciones

    public TaskEntity toEntity(Task task) {
        List<TaskProductEntity> productEntities = task.getProducts().stream()
                .map(this::toProductEntity)
                .collect(Collectors.toList());

        return TaskEntity.builder()
                .station(task.getStation())
                .tableNumber(task.getTableNumber())
                .products(productEntities)
                .build();
    }

    private TaskProductEntity toProductEntity(Product product) {
        return TaskProductEntity.builder()
                .name(product.getName())
                .type(product.getType())
                .build();
    }

    public Task toDomain(TaskEntity entity) {
        List<Product> products = entity.getProducts().stream()
                .map(this::toProduct)
                .collect(Collectors.toList());

        return new Task(
                entity.getStation(),
                entity.getTableNumber(),
                products
        );
    }

    private Product toProduct(TaskProductEntity entity) {
        return new Product(entity.getName(), entity.getType());
    }
}