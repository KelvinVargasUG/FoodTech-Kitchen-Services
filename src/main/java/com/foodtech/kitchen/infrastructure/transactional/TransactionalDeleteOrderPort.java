package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.in.DeleteOrderPort;
import com.foodtech.kitchen.application.usecases.DeleteOrderUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalDeleteOrderPort implements DeleteOrderPort {

    private final DeleteOrderUseCase delegate;

    public TransactionalDeleteOrderPort(DeleteOrderUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public void execute(Long orderId) {
        delegate.execute(orderId);
    }
}
