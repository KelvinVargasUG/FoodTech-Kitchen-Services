package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class OrderCompletionServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCompletionService service;

    @Test
    void completeOrderIfReady_whenNoTasks_returnsEarly() {
        // Arrange
        when(taskRepository.countByOrderId(10L)).thenReturn(0L);

        // Act
        service.completeOrderIfReady(10L);

        // Assert
        verify(taskRepository).countByOrderId(10L);
        verify(taskRepository, never()).countByOrderIdAndStatus(10L, TaskStatus.COMPLETED);
        verify(orderRepository, never()).findById(10L);
    }

    @Test
    void completeOrderIfReady_whenNotAllTasksCompleted_returnsEarly() {
        // Arrange
        when(taskRepository.countByOrderId(20L)).thenReturn(3L);
        when(taskRepository.countByOrderIdAndStatus(20L, TaskStatus.COMPLETED)).thenReturn(2L);

        // Act
        service.completeOrderIfReady(20L);

        // Assert
        verify(orderRepository, never()).findById(20L);
        verify(orderRepository, never()).save(org.mockito.Mockito.any(Order.class));
    }

    @Test
    void completeOrderIfReady_whenOrderMissing_throwsException() {
        // Arrange
        when(taskRepository.countByOrderId(30L)).thenReturn(1L);
        when(taskRepository.countByOrderIdAndStatus(30L, TaskStatus.COMPLETED)).thenReturn(1L);
        when(orderRepository.findById(30L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(OrderNotFoundException.class, () -> service.completeOrderIfReady(30L));
    }

    @Test
    void completeOrderIfReady_whenAlreadyCompleted_returnsEarly() {
        // Arrange
        Order completedOrder = Order.reconstruct(40L, "A1", "Cliente Test", "test@test.com", sampleProducts(), OrderStatus.COMPLETED);
        when(taskRepository.countByOrderId(40L)).thenReturn(1L);
        when(taskRepository.countByOrderIdAndStatus(40L, TaskStatus.COMPLETED)).thenReturn(1L);
        when(orderRepository.findById(40L)).thenReturn(Optional.of(completedOrder));

        // Act
        service.completeOrderIfReady(40L);

        // Assert
        verify(orderRepository, never()).save(completedOrder);
    }

    @Test
    void completeOrderIfReady_whenReady_marksCompletedAndSaves() {
        // Arrange
        Order order = Order.reconstruct(50L, "B2", "Cliente Test", "test@test.com", sampleProducts(), OrderStatus.IN_PROGRESS);
        when(taskRepository.countByOrderId(50L)).thenReturn(2L);
        when(taskRepository.countByOrderIdAndStatus(50L, TaskStatus.COMPLETED)).thenReturn(2L);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        // Act
        service.completeOrderIfReady(50L);

        // Assert
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(orderRepository).save(order);
    }

    private List<Product> sampleProducts() {
        return List.of(new Product("Pizza", ProductType.HOT_DISH, 5));
    }
}
