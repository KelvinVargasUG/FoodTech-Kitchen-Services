package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.usecases.ProcessOrderUseCase;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TransactionalProcessOrderPort implements ProcessOrderPort {

    private final ProcessOrderUseCase delegate;

    public TransactionalProcessOrderPort(ProcessOrderUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public List<Task> execute(Order order) {
        return delegate.execute(order);
    }
}
