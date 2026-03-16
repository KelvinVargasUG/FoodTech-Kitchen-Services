package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DeleteOrderUseCase useCase;

    @Test
    void execute_whenOrderExists_deletesTasksAndOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = Order.reconstruct(orderId, "A1", "Cliente Test", "test@test.com", sampleProducts());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        useCase.execute(orderId);

        // Assert
        verify(taskRepository).deleteByOrderId(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void execute_whenOrderDoesNotExist_throwsNotFound() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(OrderNotFoundException.class, () -> useCase.execute(orderId));
        verify(taskRepository, never()).deleteByOrderId(orderId);
        verify(orderRepository, never()).deleteById(orderId);
    }

    private List<Product> sampleProducts() {
        return List.of(new Product("Tea", ProductType.DRINK, 5));
    }
}
