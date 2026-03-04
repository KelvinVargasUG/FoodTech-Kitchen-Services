package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.OutboxEventRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestOrderInvoiceUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private InvoicePayloadBuilder payloadBuilder;

    @InjectMocks
    private RequestOrderInvoiceUseCase useCase;

    @Test
    void execute_whenOrderNotFound_throwsException() {
        // Arrange
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(OrderNotFoundException.class, () -> useCase.execute(100L));
        verify(outboxEventRepository, never()).save(org.mockito.Mockito.any(OutboxEvent.class));
    }

    @Test
    void execute_whenOrderAlreadyInvoiced_returnsEarly() {
        // Arrange
        Order order = Order.reconstruct(101L, "T1", sampleProducts(), OrderStatus.INVOICED);
        when(orderRepository.findById(101L)).thenReturn(Optional.of(order));

        // Act
        useCase.execute(101L);

        // Assert
        verify(outboxEventRepository, never()).save(org.mockito.Mockito.any(OutboxEvent.class));
        verify(orderRepository, never()).save(order);
    }

    @Test
    void execute_whenOrderNotCompleted_throwsException() {
        // Arrange
        Order order = Order.reconstruct(102L, "T2", sampleProducts(), OrderStatus.CREATED);
        when(orderRepository.findById(102L)).thenReturn(Optional.of(order));

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> useCase.execute(102L));
        verify(outboxEventRepository, never()).save(org.mockito.Mockito.any(OutboxEvent.class));
        verify(orderRepository, never()).save(order);
    }

    @Test
    void execute_whenOrderCompleted_persistsOutboxAndMarksInvoiced() {
        // Arrange
        Order order = Order.reconstruct(103L, "T3", sampleProducts(), OrderStatus.COMPLETED);
        when(orderRepository.findById(103L)).thenReturn(Optional.of(order));
        when(payloadBuilder.build(order, 2, 2)).thenReturn("payload");

        // Act
        useCase.execute(103L);

        // Assert
        ArgumentCaptor<OutboxEvent> eventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(eventCaptor.capture());
        OutboxEvent event = eventCaptor.getValue();
        assertEquals("Order", event.getAggregateType());
        assertEquals("103", event.getAggregateId());
        assertEquals("OrderInvoiceRequested", event.getEventType());
        assertEquals("payload", event.getPayload());

        assertEquals(OrderStatus.INVOICED, order.getStatus());
        verify(orderRepository).save(order);
        verify(payloadBuilder).build(order, 2, 2);
    }

    private List<Product> sampleProducts() {
        return List.of(
                new Product("Soda", ProductType.DRINK),
                new Product("Salad", ProductType.COLD_DISH)
        );
    }
}
