package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.application.outbox.OutboxEventStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OutboxEventEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class OutboxEventEntityMapperTest {

    private final OutboxEventEntityMapper mapper = new OutboxEventEntityMapper();

    @Test
    void toEntity_mapsAllFields() {
        // Arrange
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000123");
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime nextRetryAt = LocalDateTime.of(2026, 1, 1, 11, 0);
        LocalDateTime sentAt = LocalDateTime.of(2026, 1, 1, 12, 0);
        OutboxEvent event = new OutboxEvent(
                id,
                "Order",
                "42",
                "OrderInvoiceRequested",
                "payload",
                OutboxEventStatus.FAILED,
                2,
                nextRetryAt,
                createdAt,
                sentAt,
                "error"
        );

        // Act
        OutboxEventEntity entity = mapper.toEntity(event);

        // Assert
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Order", entity.getAggregateType());
        assertEquals("42", entity.getAggregateId());
        assertEquals("OrderInvoiceRequested", entity.getEventType());
        assertEquals("payload", entity.getPayload());
        assertEquals(OutboxEventStatus.FAILED, entity.getStatus());
        assertEquals(2, entity.getAttempts());
        assertEquals(nextRetryAt, entity.getNextRetryAt());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(sentAt, entity.getSentAt());
        assertEquals("error", entity.getLastError());
    }
}
