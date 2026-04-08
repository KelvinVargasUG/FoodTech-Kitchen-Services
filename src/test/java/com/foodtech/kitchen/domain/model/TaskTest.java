package com.foodtech.kitchen.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
class TaskTest {

    @Test
    void shouldTransitionFromPendingToInPreparation() {

        Product product = new Product("Cerveza", ProductType.DRINK, 5);
        Task task = new Task(
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                LocalDateTime.now()
        );

        task.start();

        assertEquals(TaskStatus.IN_PREPARATION, task.getStatus());
        assertNotNull(task.getStartedAt());
    }

    @Test
    void shouldTransitionFromInPreparationToCompleted() {

        Product product = new Product("Cerveza", ProductType.DRINK, 5);
        Task task = new Task(
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                LocalDateTime.now()
        );
        task.start();

        task.complete();

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    void shouldNotCompleteTaskWhenNotInPreparation() {

        Product product = new Product("Cerveza", ProductType.DRINK, 5);
        Task task = new Task(
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                LocalDateTime.now()
        );

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> task.complete()
        );
        assertEquals("Task must be in IN_PREPARATION status to complete", exception.getMessage());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }
}
