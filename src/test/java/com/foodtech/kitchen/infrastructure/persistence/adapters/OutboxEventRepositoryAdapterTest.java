package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OutboxEventJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OutboxEventEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.OutboxEventEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventRepositoryAdapterTest {

    @Mock
    private OutboxEventJpaRepository jpaRepository;

    @Mock
    private OutboxEventEntityMapper mapper;

    @InjectMocks
    private OutboxEventRepositoryAdapter adapter;

    @Test
    void save_mapsAndPersistsEntity() {
        // Arrange
        OutboxEvent event = OutboxEvent.newEvent("Order", "10", "OrderInvoiceRequested", "payload");
        OutboxEventEntity entity = OutboxEventEntity.builder().build();
        when(mapper.toEntity(event)).thenReturn(entity);

        // Act
        adapter.save(event);

        // Assert
        verify(mapper).toEntity(event);
        verify(jpaRepository).save(entity);
    }
}
