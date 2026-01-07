package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.domain.services.*;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessOrderUseCaseTest {

    private ProcessOrderUseCase useCase;
    private TaskRepository taskRepository;
    private TaskDecomposer taskDecomposer;
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        commandFactory = new CommandFactory();
        taskDecomposer = new TaskDecomposer(commandFactory);
        useCase = new ProcessOrderUseCase(taskDecomposer, taskRepository);
    }

    @Test
    @DisplayName("Should process order and save tasks")
    void shouldProcessOrderAndSaveTasks() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Order order = new Order("A1", List.of(cocaCola));

        // When
        List<Task> tasks = useCase.execute(order);

        // Then
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should process mixed order and save multiple tasks")
    void shouldProcessMixedOrderAndSaveMultipleTasks() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        Order order = new Order("B2", List.of(cocaCola, pizza));

        // When
        List<Task> tasks = useCase.execute(order);

        // Then
        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).saveAll(argThat(list -> list.size() == 2));
    }

    @Test
    @DisplayName("Should propagate validation exception from TaskDecomposer")
    void shouldPropagateValidationException() {
        // Given
        Order emptyOrder = new Order("C3", List.of());

        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(emptyOrder)
        );
        verify(taskRepository, never()).saveAll(anyList());
    }
}