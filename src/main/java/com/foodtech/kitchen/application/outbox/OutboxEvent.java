package com.foodtech.kitchen.application.outbox;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class OutboxEvent {

    private final UUID id;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final String payload;
    private final OutboxEventStatus status;
    private final int attempts;
    private final LocalDateTime nextRetryAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime sentAt;
    private final String lastError;

    public OutboxEvent(UUID id,
                       String aggregateType,
                       String aggregateId,
                       String eventType,
                       String payload,
                       OutboxEventStatus status,
                       int attempts,
                       LocalDateTime nextRetryAt,
                       LocalDateTime createdAt,
                       LocalDateTime sentAt,
                       String lastError) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.attempts = attempts;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.lastError = lastError;
    }

    public static OutboxEvent newEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                payload,
                OutboxEventStatus.NEW,
                0,
                null,
                LocalDateTime.now(),
                null,
                null
        );
    }

}
