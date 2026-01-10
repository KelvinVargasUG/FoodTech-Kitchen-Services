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

    private final ProductEntityMapper productEntityMapper;

    public TaskEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public TaskEntity toEntity(Task task) {
        List<TaskProductEntity> productEntities = task.getProducts().stream()
                .map(productEntityMapper::toTaskProductEntity)
                .collect(Collectors.toList());

        return TaskEntity.builder()
                .station(task.getStation())
                .tableNumber(task.getTableNumber())
                .products(productEntities)
                .build();
    }

    public Task toDomain(TaskEntity entity) {
        List<Product> products = entity.getProducts().stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());

        return new Task(
                entity.getStation(),
                entity.getTableNumber(),
                products
        );
    }
}