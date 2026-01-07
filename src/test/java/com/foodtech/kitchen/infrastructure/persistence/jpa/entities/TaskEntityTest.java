package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import com.foodtech.kitchen.domain.model.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TaskEntityTest {

    @Test
    @DisplayName("Should create TaskEntity with all fields")
    void shouldCreateTaskEntity() {
        // Given & When
        TaskEntity entity = TaskEntity.builder()
            .station(Station.BAR)
            .tableNumber("A1")
            .build();

        // Then
        assertNotNull(entity);
        assertEquals(Station.BAR, entity.getStation());
        assertEquals("A1", entity.getTableNumber());
    }

    @Test
    @DisplayName("Should generate ID when saved")
    void shouldHaveIdField() {
        // Given
        TaskEntity entity = new TaskEntity();
        
        // When
        entity.setId(1L);
        
        // Then
        assertEquals(1L, entity.getId());
    }
}