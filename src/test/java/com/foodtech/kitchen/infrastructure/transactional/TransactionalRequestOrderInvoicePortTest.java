package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.usecases.RequestOrderInvoiceUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionalRequestOrderInvoicePortTest {

    @Mock
    private RequestOrderInvoiceUseCase delegate;

    @InjectMocks
    private TransactionalRequestOrderInvoicePort port;

    @Test
    void execute_delegatesToUseCase() {
        // Act
        port.execute(10L);

        // Assert
        verify(delegate).execute(10L);
    }
}
