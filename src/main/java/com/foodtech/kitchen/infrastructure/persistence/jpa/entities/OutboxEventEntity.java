package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import com.foodtech.kitchen.application.outbox.OutboxEventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxEventStatus status;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "last_error")
    private String lastError;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (attempts == null) {
            attempts = 0;
        }
        if (status == null) {
            status = OutboxEventStatus.NEW;
        }
    }
}
