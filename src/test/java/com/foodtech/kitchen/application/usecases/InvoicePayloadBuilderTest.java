package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import java.util.List;
import java.util.Map;
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

@ExtendWith(MockitoExtension.class)
class InvoicePayloadBuilderTest {

    @Mock
    private PayloadSerializer payloadSerializer;

    @InjectMocks
    private InvoicePayloadBuilder builder;

    @Test
    void build_whenValidOrder_buildsPayloadAndSerializes() {
        // Arrange
        Order order = Order.reconstruct(200L, "C3", sampleProducts(), OrderStatus.COMPLETED);
        when(payloadSerializer.serialize(org.mockito.Mockito.anyMap())).thenReturn("json");

        // Act
        String result = builder.build(order, 2, 2);

        // Assert
        assertEquals("json", result);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(payloadSerializer).serialize(payloadCaptor.capture());
        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals(200L, payload.get("orderId"));
        assertEquals("C3", payload.get("tableNumber"));
        assertEquals(2, payload.get("totalItems"));
        assertEquals(2, payload.get("totalAmount"));

        Object productsObj = payload.get("products");
        assertNotNull(productsObj);
        List<Map<String, String>> products = (List<Map<String, String>>) productsObj;
        assertEquals(2, products.size());
        assertEquals("Coffee", products.get(0).get("name"));
        assertEquals("DRINK", products.get(0).get("type"));
        assertEquals("Soup", products.get(1).get("name"));
        assertEquals("HOT_DISH", products.get(1).get("type"));
    }

    private List<Product> sampleProducts() {
        return List.of(
                new Product("Coffee", ProductType.DRINK),
                new Product("Soup", ProductType.HOT_DISH)
        );
    }
}
