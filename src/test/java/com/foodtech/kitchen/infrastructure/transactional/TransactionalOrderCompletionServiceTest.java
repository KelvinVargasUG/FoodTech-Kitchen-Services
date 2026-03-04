package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionalOrderCompletionServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TransactionalOrderCompletionService service;

    @Test
    void completeOrderIfReady_whenNoTasks_returnsEarly() {
        // Arrange
        when(taskRepository.countByOrderId(99L)).thenReturn(0L);

        // Act
        service.completeOrderIfReady(99L);

        // Assert
        verify(taskRepository).countByOrderId(99L);
        verify(taskRepository, never()).countByOrderIdAndStatus(99L, TaskStatus.COMPLETED);
        verify(orderRepository, never()).findById(99L);
    }
}
