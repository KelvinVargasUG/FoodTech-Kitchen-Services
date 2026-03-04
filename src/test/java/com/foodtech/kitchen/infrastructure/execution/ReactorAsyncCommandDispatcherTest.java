package com.foodtech.kitchen.infrastructure.execution;

import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.OrderCompletionService;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactorAsyncCommandDispatcherTest {

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private OrderCompletionService orderCompletionService;

    @Mock
    private Command command;

    @InjectMocks
    private ReactorAsyncCommandDispatcher dispatcher;

    @Test
    void dispatch_whenCommandSucceeds_marksTaskCompletedAndCallsOrderCompletion() throws Exception {
        // Arrange
        long taskId = 501L;
        long orderId = 601L;
        Task task = Task.reconstruct(
                taskId,
                orderId,
                Station.BAR,
                "A1",
                sampleProducts(),
                LocalDateTime.of(2026, 1, 1, 9, 0),
                TaskStatus.IN_PREPARATION,
                LocalDateTime.of(2026, 1, 1, 9, 10),
                null
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        CountDownLatch latch = new CountDownLatch(1);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            latch.countDown();
            return invocation.getArgument(0);
        });

        // Act
        dispatcher.dispatch(command, taskId);

        // Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertEquals(TaskStatus.COMPLETED, taskCaptor.getValue().getStatus());
        verify(commandExecutor).execute(command);
        verify(orderCompletionService).completeOrderIfReady(orderId);
    }

    @Test
    void dispatch_whenCommandFails_doesNotUpdateTask() throws Exception {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            throw new RuntimeException("boom");
        }).when(commandExecutor).execute(command);

        // Act
        dispatcher.dispatch(command, 777L);

        // Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        verify(taskRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
        verify(orderCompletionService, never()).completeOrderIfReady(anyLong());
    }

    private List<Product> sampleProducts() {
        return List.of(new Product("Tea", ProductType.DRINK));
    }
}
