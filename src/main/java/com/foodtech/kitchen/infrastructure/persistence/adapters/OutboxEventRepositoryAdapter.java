package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.application.ports.out.OutboxEventRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.OutboxEventJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.mappers.OutboxEventEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository jpaRepository;
    private final OutboxEventEntityMapper mapper;

    public OutboxEventRepositoryAdapter(OutboxEventJpaRepository jpaRepository, OutboxEventEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(OutboxEvent event) {
        jpaRepository.save(mapper.toEntity(event));
    }
}
