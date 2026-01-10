package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.infrastructure.persistence.jpa.TaskJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRepositoryAdapterTest {

    private TaskRepositoryAdapter adapter;
    private TaskJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(TaskJpaRepository.class);
        com.foodtech.kitchen.infrastructure.persistence.mappers.TaskEntityMapper mapper = 
            new com.foodtech.kitchen.infrastructure.persistence.mappers.TaskEntityMapper();
        adapter = new TaskRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Should save tasks using JPA repository")
    void shouldSaveTasks() {
        // Given
        Product product = new Product("Coca Cola", ProductType.DRINK);
        Task task = new Task(Station.BAR, "A1", List.of(product));

        // When
        adapter.saveAll(List.of(task));

        // Then
        verify(jpaRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should find tasks by station")
    void shouldFindTasksByStation() {
        // Given
        com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity p =
            com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity.builder()
                .name("Coca Cola").type(ProductType.DRINK).build();

        TaskEntity entity = TaskEntity.builder()
            .station(Station.BAR)
            .tableNumber("A1")
            .products(List.of(p))
            .build();
        
        when(jpaRepository.findByStation(Station.BAR))
            .thenReturn(List.of(entity));

        // When
        List<Task> tasks = adapter.findByStation(Station.BAR);

        // Then
        assertEquals(1, tasks.size());
        assertEquals(Station.BAR, tasks.get(0).getStation());
        verify(jpaRepository, times(1)).findByStation(Station.BAR);
    }

    @Test
    @DisplayName("Should find all tasks")
    void shouldFindAllTasks() {
        // Given
        com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity p =
            com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity.builder()
                .name("Coca Cola").type(ProductType.DRINK).build();

        TaskEntity entity = TaskEntity.builder()
            .station(Station.BAR)
            .tableNumber("A1")
            .products(List.of(p))
            .build();
        
        when(jpaRepository.findAll()).thenReturn(List.of(entity));

        // When
        List<Task> tasks = adapter.findAll();

        // Then
        assertEquals(1, tasks.size());
        verify(jpaRepository, times(1)).findAll();
    }
}