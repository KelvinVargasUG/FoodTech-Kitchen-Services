package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class GetTasksByStationUseCaseTest {

    private GetTasksByStationUseCase useCase;
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        useCase = new GetTasksByStationUseCase(taskRepository);
    }

    @Test
    @DisplayName("Should return only tasks for specified station")
    void shouldReturnOnlyTasksForSpecifiedStation() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product sprite = new Product("Sprite", ProductType.DRINK, 5);

        LocalDateTime now = LocalDateTime.now();
        Task barTask1 = new Task(1L, Station.BAR, "A1", List.of(cocaCola), now);
        Task barTask2 = new Task(1L, Station.BAR, "A2", List.of(sprite), now);

        when(taskRepository.findByStation(Station.BAR))
            .thenReturn(List.of(barTask1, barTask2));

        List<Task> tasks = useCase.execute(Station.BAR, null);

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getStation() == Station.BAR));
        verify(taskRepository, times(1)).findByStation(Station.BAR);
    }

    @Test
    @DisplayName("Should return empty list when no tasks for station")
    void shouldReturnEmptyListWhenNoTasksForStation() {

        when(taskRepository.findByStation(Station.BAR))
            .thenReturn(List.of());

        List<Task> tasks = useCase.execute(Station.BAR, null);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
        verify(taskRepository, times(1)).findByStation(Station.BAR);
    }
}
