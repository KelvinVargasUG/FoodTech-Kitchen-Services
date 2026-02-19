package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.domain.services.OrderStatusCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetOrderStatusUseCaseTest {

    private GetOrderStatusUseCase useCase;
    private TaskRepository taskRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        orderRepository = mock(OrderRepository.class);
        useCase = new GetOrderStatusUseCase(taskRepository, orderRepository, new OrderStatusCalculator());
    }

    @Test
    @DisplayName("Should return COMPLETED when all tasks are completed")
    void shouldReturnCompletedWhenAllTasksCompleted() {
        // Given - un pedido con 3 tareas completadas
        Long orderId = 1L;
        Product product = new Product("Pizza", ProductType.HOT_DISH);
        
        Task completedTask1 = Task.reconstruct(1L, orderId, Station.BAR, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());
        Task completedTask2 = Task.reconstruct(2L, orderId, Station.HOT_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());
        Task completedTask3 = Task.reconstruct(3L, orderId, Station.COLD_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());

        Order order = Order.reconstruct(orderId, "A1", List.of(product), OrderStatus.COMPLETED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        when(taskRepository.findByOrderId(orderId))
            .thenReturn(List.of(completedTask1, completedTask2, completedTask3));

        // When
        TaskStatus orderStatus = useCase.execute(orderId);

        // Then
        assertEquals(TaskStatus.COMPLETED, orderStatus);
        verify(taskRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("Should return IN_PREPARATION when at least one task is in preparation")
    void shouldReturnInPreparationWhenAnyTaskInPreparation() {
        // Given - un pedido con 2 tareas completadas y 1 en preparación
        Long orderId = 1L;
        Product product = new Product("Pizza", ProductType.HOT_DISH);
        
        Task completedTask1 = Task.reconstruct(1L, orderId, Station.BAR, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());
        Task completedTask2 = Task.reconstruct(2L, orderId, Station.HOT_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());
        Task inPreparationTask = Task.reconstruct(3L, orderId, Station.COLD_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.IN_PREPARATION, 
            LocalDateTime.now(), null);

        Order order = Order.reconstruct(orderId, "A1", List.of(product), OrderStatus.IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        when(taskRepository.findByOrderId(orderId))
            .thenReturn(List.of(completedTask1, completedTask2, inPreparationTask));

        // When
        TaskStatus orderStatus = useCase.execute(orderId);

        // Then
        assertEquals(TaskStatus.IN_PREPARATION, orderStatus);
        verify(taskRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("Should return PENDING when all tasks are pending")
    void shouldReturnPendingWhenAllTasksPending() {
        // Given - un pedido con todas las tareas pendientes
        Long orderId = 1L;
        Product product = new Product("Pizza", ProductType.HOT_DISH);
        
        Task pendingTask1 = Task.reconstruct(1L, orderId, Station.BAR, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.PENDING, null, null);
        Task pendingTask2 = Task.reconstruct(2L, orderId, Station.HOT_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.PENDING, null, null);

        Order order = Order.reconstruct(orderId, "A1", List.of(product), OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        when(taskRepository.findByOrderId(orderId))
            .thenReturn(List.of(pendingTask1, pendingTask2));

        // When
        TaskStatus orderStatus = useCase.execute(orderId);

        // Then
        assertEquals(TaskStatus.PENDING, orderStatus);
        verify(taskRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("Should return IN_PREPARATION when there are pending and in preparation tasks")
    void shouldReturnInPreparationWhenMixedStates() {
        // Given - un pedido con tareas pendientes y en preparación
        Long orderId = 1L;
        Product product = new Product("Pizza", ProductType.HOT_DISH);
        
        Task pendingTask = Task.reconstruct(1L, orderId, Station.BAR, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.PENDING, null, null);
        Task inPreparationTask = Task.reconstruct(2L, orderId, Station.HOT_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.IN_PREPARATION, 
            LocalDateTime.now(), null);

        Order order = Order.reconstruct(orderId, "A1", List.of(product), OrderStatus.IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        when(taskRepository.findByOrderId(orderId))
            .thenReturn(List.of(pendingTask, inPreparationTask));

        // When
        TaskStatus orderStatus = useCase.execute(orderId);

        // Then
        assertEquals(TaskStatus.IN_PREPARATION, orderStatus);
        verify(taskRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("Should return IN_PREPARATION when some tasks are completed but not all")
    void shouldReturnInPreparationWhenSomeTasksCompleted() {
        // Given - un pedido con 1 tarea completada y 2 pendientes (caso Postman)
        Long orderId = 1L;
        Product product = new Product("Pizza", ProductType.HOT_DISH);
        
        Task completedTask = Task.reconstruct(1L, orderId, Station.BAR, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.COMPLETED, 
            LocalDateTime.now(), LocalDateTime.now());
        Task pendingTask1 = Task.reconstruct(2L, orderId, Station.HOT_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.PENDING, null, null);
        Task pendingTask2 = Task.reconstruct(3L, orderId, Station.COLD_KITCHEN, "A1", 
            List.of(product), LocalDateTime.now(), TaskStatus.PENDING, null, null);

        Order order = Order.reconstruct(orderId, "A1", List.of(product), OrderStatus.IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        when(taskRepository.findByOrderId(orderId))
            .thenReturn(List.of(completedTask, pendingTask1, pendingTask2));

        // When
        TaskStatus orderStatus = useCase.execute(orderId);

        // Then
        assertEquals(TaskStatus.IN_PREPARATION, orderStatus);
        verify(taskRepository, times(1)).findByOrderId(orderId);
    }
}
