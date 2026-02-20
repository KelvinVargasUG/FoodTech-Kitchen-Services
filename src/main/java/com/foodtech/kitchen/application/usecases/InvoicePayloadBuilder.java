package com.foodtech.kitchen.application.usecases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InvoicePayloadBuilder {

    private final ObjectMapper objectMapper;

    public InvoicePayloadBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to build invoice payload", ex);
        }
    }

    private Map<String, String> toProductPayload(Product product) {
        return Map.of(
                "name", product.getName(),
                "type", product.getType().name()
        );
    }
}
