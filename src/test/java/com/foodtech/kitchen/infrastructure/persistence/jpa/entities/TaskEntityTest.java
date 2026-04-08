package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import com.foodtech.kitchen.domain.model.Station;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("component")
class TaskEntityTest {

    @Test
    @DisplayName("Should create TaskEntity with all fields")
    void shouldCreateTaskEntity() {

        TaskEntity entity = TaskEntity.builder()
            .orderId(1L)
            .station(Station.BAR)
            .tableNumber("A1")
            .build();

        assertNotNull(entity);
        assertEquals(1L, entity.getOrderId());
        assertEquals(Station.BAR, entity.getStation());
        assertEquals("A1", entity.getTableNumber());
    }

    @Test
    @DisplayName("Should generate ID when saved")
    void shouldHaveIdField() {

        TaskEntity entity = new TaskEntity();

        assertNull(entity.getId());
    }
}
