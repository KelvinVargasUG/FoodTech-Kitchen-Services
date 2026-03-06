package com.foodtech.kitchen.application.outbox;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("unit")
class OutboxEventTest {

    @Test
    void newEvent_setsDefaultFields() {
        // Arrange
        String aggregateType = "Order";
        String aggregateId = "123";
        String eventType = "OrderInvoiceRequested";
        String payload = "payload";

        // Act
        OutboxEvent event = OutboxEvent.newEvent(aggregateType, aggregateId, eventType, payload);

        // Assert
        assertNotNull(event.getId());
        assertEquals(aggregateType, event.getAggregateType());
        assertEquals(aggregateId, event.getAggregateId());
        assertEquals(eventType, event.getEventType());
        assertEquals(payload, event.getPayload());
        assertEquals(OutboxEventStatus.NEW, event.getStatus());
        assertEquals(0, event.getAttempts());
        assertNull(event.getNextRetryAt());
        assertNotNull(event.getCreatedAt());
        assertNull(event.getSentAt());
        assertNull(event.getLastError());
    }
}
