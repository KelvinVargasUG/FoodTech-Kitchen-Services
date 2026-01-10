package com.foodtech.kitchen.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldTransitionFromPendingToInPreparation() {
        // Given
        Product product = new Product("Cerveza", ProductType.DRINK);
        Task task = new Task(
                1L,
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                LocalDateTime.now()
        );

        // When
        task.start();

        // Then
        assertEquals(TaskStatus.IN_PREPARATION, task.getStatus());
        assertNotNull(task.getStartedAt());
    }
}
