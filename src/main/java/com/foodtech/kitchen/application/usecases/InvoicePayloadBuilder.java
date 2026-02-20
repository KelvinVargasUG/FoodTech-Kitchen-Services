package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoicePayloadBuilder {

    private final PayloadSerializer payloadSerializer;

    public InvoicePayloadBuilder(PayloadSerializer payloadSerializer) {
        this.payloadSerializer = payloadSerializer;
    }

    public String build(Order order, int totalItems, int totalAmount) {
        List<Map<String, String>> products = order.getProducts().stream()
                .map(this::toProductPayload)
                .collect(Collectors.toList());

        Map<String, Object> payload = Map.of(
                "orderId", order.getId(),
                "tableNumber", order.getTableNumber(),
                "totalItems", totalItems,
                "totalAmount", totalAmount,
                "products", products
        );

        return payloadSerializer.serialize(payload);
    }

    private Map<String, String> toProductPayload(Product product) {
        return Map.of(
                "name", product.getName(),
                "type", product.getType().name()
        );
    }
}
