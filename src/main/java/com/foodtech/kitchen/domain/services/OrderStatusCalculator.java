package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;

import java.util.List;

public class OrderStatusCalculator {

    public TaskStatus calculateOrderStatus(List<Task> tasks) {
        return calculateOrderStatus(null, tasks);
    }

    public TaskStatus calculateOrderStatus(OrderStatus persistedStatus, List<Task> tasks) {
        if (persistedStatus != null) {
            return mapToTaskStatus(persistedStatus);
        }

        validateTasks(tasks);

        if (areAllTasksCompleted(tasks)) {
            return TaskStatus.COMPLETED;
        }

        if (hasAnyTaskStarted(tasks)) {
            return TaskStatus.IN_PREPARATION;
        }

        return TaskStatus.PENDING;
    }

    private TaskStatus mapToTaskStatus(OrderStatus status) {
        return switch (status) {
            case CREATED -> TaskStatus.PENDING;
            case IN_PROGRESS -> TaskStatus.IN_PREPARATION;
            case COMPLETED -> TaskStatus.COMPLETED;
            case INVOICED -> TaskStatus.COMPLETED;
        };
    }

    private void validateTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate status for empty task list");
        }
    }

    private boolean hasAnyTaskStarted(List<Task> tasks) {
        return tasks.stream()
                .anyMatch(task -> task.getStatus() == TaskStatus.IN_PREPARATION 
                               || task.getStatus() == TaskStatus.COMPLETED);
    }

    private boolean areAllTasksCompleted(List<Task> tasks) {
        return tasks.stream()
                .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);
    }
}
