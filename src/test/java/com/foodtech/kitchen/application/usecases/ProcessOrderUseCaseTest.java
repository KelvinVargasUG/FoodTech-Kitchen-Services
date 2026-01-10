package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.domain.commands.Command;
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
    private CommandExecutor commandExecutor;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        commandFactory = new CommandFactory();
        commandExecutor = mock(CommandExecutor.class);
        
        OrderValidator orderValidator = new OrderValidator();
        TaskFactory taskFactory = new TaskFactory();
        taskDecomposer = new TaskDecomposer(orderValidator, taskFactory);
        
        useCase = new ProcessOrderUseCase(taskDecomposer, taskRepository, commandFactory, commandExecutor);
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
        verify(commandExecutor, times(1)).executeAll(anyList());
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
        // When & Then - la validación ya se lanza al crear el Order
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(new Order("C3", List.of()))
        );
        verify(taskRepository, never()).saveAll(anyList());
    }
}