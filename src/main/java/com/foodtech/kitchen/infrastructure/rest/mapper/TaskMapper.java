package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.infrastructure.rest.dto.TaskResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        List<Map<String, String>> products = task.getProducts().stream()
            .map(product -> Map.of(
                "name", product.getName(),
                "type", product.getType().name()
            ))
            .collect(Collectors.toList());

        return new TaskResponse(
            task.getId(),
            task.getOrderId(),
            task.getStation().name(),
            task.getTableNumber(),
            products,
            task.getCreatedAt(),
            task.getStatus().name(),
            task.getStartedAt()
        );
    }

    public static List<TaskResponse> toResponseList(List<Task> tasks) {
        return tasks.stream()
            .map(TaskMapper::toResponse)
            .collect(Collectors.toList());
    }
}
