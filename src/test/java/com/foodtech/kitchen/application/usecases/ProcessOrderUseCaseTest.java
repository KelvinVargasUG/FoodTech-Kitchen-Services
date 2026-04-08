package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.services.OrderValidator;
import com.foodtech.kitchen.domain.services.TaskDecomposer;
import com.foodtech.kitchen.domain.services.TaskFactory;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class ProcessOrderUseCaseTest {

    private ProcessOrderUseCase useCase;
    private OrderRepository orderRepository;
    private TaskRepository taskRepository;
    private TaskDecomposer taskDecomposer;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        taskRepository = mock(TaskRepository.class);

        OrderValidator orderValidator = new OrderValidator();
        TaskFactory taskFactory = new TaskFactory();
        taskDecomposer = new TaskDecomposer(orderValidator, taskFactory);

        useCase = new ProcessOrderUseCase(orderRepository, taskDecomposer, taskRepository);
    }

    @Test
    @DisplayName("Should process order and save tasks")
    void shouldProcessOrderAndSaveTasks() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Order order = new Order("A1", "Cliente Test", "test@test.com", List.of(cocaCola));
        Order savedOrder = Order.reconstruct(1L, "A1", "Cliente Test", "test@test.com", List.of(cocaCola));

        when(orderRepository.save(order)).thenReturn(savedOrder);

        List<Task> tasks = useCase.execute(order);

        assertEquals(1, tasks.size());
        verify(orderRepository, times(1)).save(order);
        verify(taskRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should process mixed order and save multiple tasks")
    void shouldProcessMixedOrderAndSaveMultipleTasks() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        Order order = new Order("B2", "Cliente Test", "test@test.com", List.of(cocaCola, pizza));
        Order savedOrder = Order.reconstruct(2L, "B2", "Cliente Test", "test@test.com", List.of(cocaCola, pizza));

        when(orderRepository.save(order)).thenReturn(savedOrder);

        List<Task> tasks = useCase.execute(order);

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).saveAll(argThat(list -> list.size() == 2));
    }

    @Test
    @DisplayName("Should propagate validation exception from TaskDecomposer")
    void shouldPropagateValidationException() {

        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(new Order("C3", "Cliente Test", "test@test.com", List.of()))
        );
        verify(taskRepository, never()).saveAll(anyList());
    }
}
