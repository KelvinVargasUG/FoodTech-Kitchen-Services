package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.outbox.OutboxEvent;
import com.foodtech.kitchen.application.ports.in.RequestOrderInvoicePort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.OutboxEventRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;

public class RequestOrderInvoiceUseCase implements RequestOrderInvoicePort {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final InvoicePayloadBuilder payloadBuilder;

    public RequestOrderInvoiceUseCase(OrderRepository orderRepository,
                                      OutboxEventRepository outboxEventRepository,
                                      InvoicePayloadBuilder payloadBuilder) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.payloadBuilder = payloadBuilder;
    }

    @Override
    public void execute(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.INVOICED) {
            return;
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order must be completed to request invoice");
        }

        int totalItems = order.getProducts().size();
        int totalAmount = totalItems;
        String payload = payloadBuilder.build(order, totalItems, totalAmount);

        OutboxEvent event = OutboxEvent.newEvent(
                "Order",
                orderId.toString(),
                "OrderInvoiceRequested",
                payload
        );
        outboxEventRepository.save(event);

        order.markInvoiced();
        orderRepository.save(order);
    }
}
