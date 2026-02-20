package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.application.outbox.OutboxEvent;

public interface OutboxEventRepository {
    void save(OutboxEvent event);
}
