package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.in.RequestOrderInvoicePort;
import com.foodtech.kitchen.application.usecases.RequestOrderInvoiceUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalRequestOrderInvoicePort implements RequestOrderInvoicePort {

    private final RequestOrderInvoiceUseCase delegate;

    public TransactionalRequestOrderInvoicePort(RequestOrderInvoiceUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public void execute(Long orderId) {
        delegate.execute(orderId);
    }
}
