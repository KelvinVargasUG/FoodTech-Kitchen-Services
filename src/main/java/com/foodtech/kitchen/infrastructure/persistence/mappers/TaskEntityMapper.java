package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//HUMAN REVIEW: Extraje la lógica de serialización/deserialización JSON a mapper dedicado.
//Cumple SRP: TaskRepositoryAdapter solo adapta JPA, este mapper solo serializa/deserializa.
//Elimina duplicación: ProductDto centralizado, evita duplicación con OrderEntityMapper.
@Component
public class TaskEntityMapper {

    private final ObjectMapper objectMapper;

    public TaskEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TaskEntity toEntity(Task task) {
        try {
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

    public Task toDomain(TaskEntity entity) {
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

    public record ProductDto(String name, String type) {}
}
