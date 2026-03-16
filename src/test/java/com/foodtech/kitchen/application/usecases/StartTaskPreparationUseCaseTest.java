package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import com.foodtech.kitchen.domain.ports.out.AsyncCommandDispatcher;
import com.foodtech.kitchen.domain.services.CommandFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class StartTaskPreparationUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CommandFactory commandFactory;

    @Mock
    private AsyncCommandDispatcher asyncCommandDispatcher;

    @InjectMocks
    private StartTaskPreparationUseCase useCase;

    @Test
    void shouldStartTaskAndDispatchCommand() {
        // Given
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.of(2026, 2, 20, 12, 0);
        Product product = new Product("Cerveza", ProductType.DRINK, 5);
        Task pendingTask = Task.reconstruct(
                taskId,
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                now,
                TaskStatus.PENDING,
                null,
                null
        );

        Task inPreparationTask = Task.reconstruct(
                taskId,
                1L,
                Station.BAR,
                "A1",
                List.of(product),
                now,
                TaskStatus.IN_PREPARATION,
                now,
                null
            );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(pendingTask), Optional.of(inPreparationTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(Order.reconstruct(1L, "A1", "Cliente Test", "test@test.com", List.of(product), OrderStatus.CREATED)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Command command = mock(Command.class);
        when(commandFactory.createCommand(any(), any())).thenReturn(command);

        // When
        Task result = useCase.execute(taskId);

        // Then
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PREPARATION, result.getStatus());
        assertNotNull(result.getStartedAt());
        verify(taskRepository, atLeastOnce()).findById(taskId);
        verify(taskRepository, atLeastOnce()).save(any(Task.class));
        verify(asyncCommandDispatcher).dispatch(command, taskId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(TaskNotFoundException.class, () -> useCase.execute(taskId));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(asyncCommandDispatcher);
    }
}
