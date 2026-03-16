package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoicePayloadBuilder {

    private final PayloadSerializer payloadSerializer;

    public InvoicePayloadBuilder(PayloadSerializer payloadSerializer) {
        this.payloadSerializer = payloadSerializer;
    }

    public String build(Order order) {
        Map<String, int[]> productAccumulator = new HashMap<>();
        for (Product product : order.getProducts()) {
            productAccumulator.compute(product.getName(), (name, acc) -> {
                if (acc == null) return new int[]{product.getPrice(), 1};
                acc[1]++;
                return acc;
            });
        }

        List<Map<String, Object>> listaProductos = new ArrayList<>();
        int total = 0;

        for (Map.Entry<String, int[]> entry : productAccumulator.entrySet()) {
            int precio = entry.getValue()[0];
            int cantidad = entry.getValue()[1];
            listaProductos.add(Map.of(
                    "nombre", entry.getKey(),
                    "precio", precio,
                    "cantidad", cantidad
            ));
            total += precio * cantidad;
        }

        Map<String, Object> payload = Map.of(
                "total", total,
                "formato", "PDF",
                "emailCliente", order.getCustomerEmail(),
                "nombreCliente", order.getCustomerName(),
                "listaProductos", listaProductos
        );

        return payloadSerializer.serialize(payload);
    }
}
