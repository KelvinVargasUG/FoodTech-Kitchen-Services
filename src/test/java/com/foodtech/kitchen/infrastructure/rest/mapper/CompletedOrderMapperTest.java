package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;
import com.foodtech.kitchen.infrastructure.rest.dto.CompletedOrderResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class CompletedOrderMapperTest {

    @Test
    void toResponse_mapsFields() {
        // Arrange
        LocalDateTime completedAt = LocalDateTime.of(2026, 2, 1, 12, 30);
        CompletedOrderView view = new CompletedOrderView(1L, "A1", completedAt, 2, 120L);

        // Act
        CompletedOrderResponse response = CompletedOrderMapper.toResponse(view);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.orderId());
        assertEquals("A1", response.tableNumber());
        assertEquals(completedAt, response.completedAt());
        assertEquals(2, response.totalItems());
        assertEquals(120L, response.totalPreparationTime());
    }

    @Test
    void toResponseList_mapsAllItems() {
        // Arrange
        CompletedOrderView first = new CompletedOrderView(1L, "A1", null, 1, null);
        CompletedOrderView second = new CompletedOrderView(2L, "B2", null, 3, 300L);

        // Act
        List<CompletedOrderResponse> responses = CompletedOrderMapper.toResponseList(List.of(first, second));

        // Assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).orderId());
        assertEquals(2L, responses.get(1).orderId());
    }
}
