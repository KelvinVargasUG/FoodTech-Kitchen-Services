package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import com.foodtech.kitchen.application.outbox.OutboxEventStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class OutboxEventEntityTest {

    private void invokeOnCreate(OutboxEventEntity entity) throws Exception {
        Method method = OutboxEventEntity.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(entity);
    }

    @Test
    void onCreate_setsDefaults_whenFieldsAreNull() throws Exception {
        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("Order")
                .aggregateId("1")
                .eventType("ORDER_CREATED")
                .payload("{}")
                .build();

        invokeOnCreate(entity);

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getAttempts()).isZero();
        assertThat(entity.getStatus()).isEqualTo(OutboxEventStatus.NEW);
    }

    @Test
    void onCreate_doesNotOverwrite_whenFieldsAlreadySet() throws Exception {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("Product")
                .aggregateId("42")
                .eventType("PRODUCT_UPDATED")
                .payload("{\"id\":\"42\"}")
                .status(OutboxEventStatus.SENT)
                .attempts(3)
                .createdAt(fixedTime)
                .build();

        invokeOnCreate(entity);

        assertThat(entity.getCreatedAt()).isEqualTo(fixedTime);
        assertThat(entity.getAttempts()).isEqualTo(3);
        assertThat(entity.getStatus()).isEqualTo(OutboxEventStatus.SENT);
    }

    @Test
    void builder_setsAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(id)
                .aggregateType("Order")
                .aggregateId("10")
                .eventType("ORDER_COMPLETED")
                .payload("{\"orderId\":\"10\"}")
                .status(OutboxEventStatus.NEW)
                .attempts(0)
                .nextRetryAt(now)
                .createdAt(now)
                .sentAt(now)
                .lastError(null)
                .build();

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getAggregateType()).isEqualTo("Order");
        assertThat(entity.getEventType()).isEqualTo("ORDER_COMPLETED");
        assertThat(entity.getStatus()).isEqualTo(OutboxEventStatus.NEW);
        assertThat(entity.getAttempts()).isZero();
    }
}
