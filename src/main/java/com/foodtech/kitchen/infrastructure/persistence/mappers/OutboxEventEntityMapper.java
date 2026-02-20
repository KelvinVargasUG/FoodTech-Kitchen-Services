package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OutboxEventEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventEntityMapper {

    public OutboxEventEntity toEntity(OutboxEvent event) {
        return OutboxEventEntity.builder()
                .id(event.getId())
                .aggregateType(event.getAggregateType())
                .aggregateId(event.getAggregateId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .status(event.getStatus())
                .attempts(event.getAttempts())
                .nextRetryAt(event.getNextRetryAt())
                .createdAt(event.getCreatedAt())
                .sentAt(event.getSentAt())
                .lastError(event.getLastError())
                .build();
    }
}
