package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class InvoicePayloadBuilderTest {

    @Mock
    private PayloadSerializer payloadSerializer;

    @InjectMocks
    private InvoicePayloadBuilder builder;

    @Test
    void build_whenValidOrder_buildsPayloadAndSerializes() {

        Order order = Order.reconstruct(200L,
                "C3", "Cliente Test", "test@test.com", sampleProducts(), OrderStatus.COMPLETED);
        when(payloadSerializer.serialize(org.mockito.Mockito.anyMap())).thenReturn("json");

        String result = builder.build(order);

        assertEquals("json", result);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(payloadSerializer).serialize(payloadCaptor.capture());
        Map<String, Object> payload = payloadCaptor.getValue();

        assertEquals(10, payload.get("total")); 
        assertEquals("PDF", payload.get("formato"));
        assertEquals("test@test.com", payload.get("emailCliente"));
        assertEquals("Cliente Test", payload.get("nombreCliente"));

        Object productsObj = payload.get("listaProductos");
        assertNotNull(productsObj);
        List<Map<String, Object>> products = (List<Map<String, Object>>) productsObj;
        assertEquals(2, products.size());

        long fuerteCount = products.stream().filter(p -> p.get("nombre").equals("Plato fuerte")).count();
        long entradaCount = products.stream().filter(p -> p.get("nombre").equals("Plato entrada")).count();

        assertEquals(1, fuerteCount);
        assertEquals(1, entradaCount);
    }

    private List<Product> sampleProducts() {
        return List.of(
                new Product("Plato fuerte", ProductType.HOT_DISH, 5),
                new Product("Plato entrada", ProductType.COLD_DISH, 5)
        );
    }
}
