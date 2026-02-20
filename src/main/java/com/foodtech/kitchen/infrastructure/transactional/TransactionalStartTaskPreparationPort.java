package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.in.StartTaskPreparationPort;
import com.foodtech.kitchen.application.usecases.StartTaskPreparationUseCase;
import com.foodtech.kitchen.domain.model.Task;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalStartTaskPreparationPort implements StartTaskPreparationPort {

    private final StartTaskPreparationUseCase delegate;

    public TransactionalStartTaskPreparationPort(StartTaskPreparationUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public Task execute(Long taskId) {
        return delegate.execute(taskId);
    }
}
