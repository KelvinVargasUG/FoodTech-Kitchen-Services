package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompletedOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private GetCompletedOrdersUseCase useCase;

    @Test
    void execute_whenCompletedOrdersExist_returnsViewsWithTiming() {
        // Arrange
        Order order = Order.reconstruct(1L, "A1", sampleProducts(), OrderStatus.COMPLETED);
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 9, 50);
        LocalDateTime startedAt1 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime startedAt2 = LocalDateTime.of(2026, 1, 1, 10, 5);
        LocalDateTime completedAt1 = LocalDateTime.of(2026, 1, 1, 10, 20);
        LocalDateTime completedAt2 = LocalDateTime.of(2026, 1, 1, 10, 30);

        Task task1 = Task.reconstruct(
                11L,
                1L,
                Station.BAR,
                "A1",
                sampleProducts(),
                createdAt,
                TaskStatus.COMPLETED,
                startedAt1,
                completedAt1
        );
        Task task2 = Task.reconstruct(
                12L,
                1L,
                Station.HOT_KITCHEN,
                "A1",
                sampleProducts(),
                createdAt,
                TaskStatus.COMPLETED,
                startedAt2,
                completedAt2
        );

        when(orderRepository.findByStatus(OrderStatus.COMPLETED))
                .thenReturn(List.of(order));
        when(taskRepository.findByOrderId(1L))
                .thenReturn(List.of(task1, task2));

        // Act
        List<CompletedOrderView> result = useCase.execute();

        // Assert
        assertEquals(1, result.size());
        CompletedOrderView view = result.get(0);
        assertEquals(1L, view.orderId());
        assertEquals("A1", view.tableNumber());
        assertEquals(2, view.totalItems());
        assertEquals(completedAt2, view.completedAt());
        assertEquals(1800L, view.totalPreparationTime());
        assertNotNull(view.completedAt());

        verify(orderRepository).findByStatus(OrderStatus.COMPLETED);
        verify(taskRepository).findByOrderId(1L);
    }

    @Test
    void execute_whenNoCompletedTasks_returnsNullTiming() {
        // Arrange
        Order order = Order.reconstruct(2L, "B2", sampleProducts(), OrderStatus.COMPLETED);
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 9, 50);
        Task task = Task.reconstruct(
                21L,
                2L,
                Station.COLD_KITCHEN,
                "B2",
                sampleProducts(),
                createdAt,
                TaskStatus.IN_PREPARATION,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                null
        );

        when(orderRepository.findByStatus(OrderStatus.COMPLETED))
                .thenReturn(List.of(order));
        when(taskRepository.findByOrderId(2L))
                .thenReturn(List.of(task));

        // Act
        List<CompletedOrderView> result = useCase.execute();

        // Assert
        CompletedOrderView view = result.get(0);
        assertNull(view.completedAt());
        assertNull(view.totalPreparationTime());
        assertEquals(2, view.totalItems());

        verify(taskRepository).findByOrderId(2L);
    }

    private List<Product> sampleProducts() {
        return List.of(
                new Product("Burger", ProductType.HOT_DISH),
                new Product("Soda", ProductType.DRINK)
        );
    }
}
