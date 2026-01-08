package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.infrastructure.persistence.jpa.TaskJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskRepositoryAdapter implements TaskRepository {

    private final TaskJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public TaskRepositoryAdapter(TaskJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void saveAll(List<Task> tasks) {
        List<TaskEntity> entities = tasks.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<Task> findByStation(Station station) {
        return jpaRepository.findByStation(station).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    private TaskEntity toEntity(Task task) {
        try {
            // Extract table number from first product or default
            String tableNumber = "UNKNOWN";
            
            String productsJson = objectMapper.writeValueAsString(
                task.getProducts().stream()
                    .map(p -> new ProductDto(p.getName(), p.getType().name()))
                    .collect(Collectors.toList())
            );

            return TaskEntity.builder()
                .station(task.getStation())
                .tableNumber(tableNumber)
                .productsJson(productsJson)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting task to entity", e);
        }
    }

    private Task toDomain(TaskEntity entity) {
        try {
            List<ProductDto> productDtos = objectMapper.readValue(
                entity.getProductsJson(),
                new TypeReference<List<ProductDto>>() {}
            );

            List<Product> products = productDtos.stream()
                .map(dto -> new Product(dto.name(), ProductType.valueOf(dto.type())))
                .collect(Collectors.toList());

            return new Task(entity.getStation(), products);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting entity to task", e);
        }
    }

    private record ProductDto(String name, String type) {}
}