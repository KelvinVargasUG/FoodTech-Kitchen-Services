package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        // Given - 3 tareas pendientes: 2 BAR, 1 HOT_KITCHEN
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product sprite = new Product("Sprite", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        
        Task barTask1 = new Task(Station.BAR, "A1", List.of(cocaCola));
        Task barTask2 = new Task(Station.BAR, "A2", List.of(sprite));
        Task hotKitchenTask = new Task(Station.HOT_KITCHEN, "B1", List.of(pizza));
        
        when(taskRepository.findByStation(Station.BAR))
            .thenReturn(List.of(barTask1, barTask2));

        // When - el encargado de barra consulta sus tareas
        List<Task> tasks = useCase.execute(Station.BAR);

        // Then - el sistema muestra únicamente las 2 tareas de barra
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(task -> task.getStation() == Station.BAR));
        verify(taskRepository, times(1)).findByStation(Station.BAR);
    }
}
