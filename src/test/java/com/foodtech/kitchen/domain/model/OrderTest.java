package com.foodtech.kitchen.domain.model;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
class OrderTest {

    @Test
    void constructor_whenTableNumberNull_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(null, "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5))));
    }

    @Test
    void constructor_whenTableNumberBlank_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("  ", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5))));
    }

    @Test
    void constructor_whenProductsNull_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("A1", "Cliente Test", "test@test.com", null));
    }

    @Test
    void constructor_whenProductsEmpty_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order("A1", "Cliente Test", "test@test.com", List.of()));
    }

    @Test
    void reconstruct_whenIdNull_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> Order.reconstruct(null, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5))));
    }

    @Test
    void reconstruct_whenStatusNull_defaultsToCreated() {
        Order order = Order.reconstruct(1L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), null);

        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void reconstruct_whenStatusProvided_setsStatus() {
        Order order = Order.reconstruct(2L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.IN_PROGRESS);

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    void markInProgress_whenCreated_changesStatus() {
        Order order = new Order("A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)));

        order.markInProgress();

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    void markInProgress_whenNotCreated_keepsStatus() {
        Order order = Order.reconstruct(3L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.IN_PROGRESS);

        order.markInProgress();

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    void markCompleted_whenNotCompleted_setsCompleted() {
        Order order = Order.reconstruct(4L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.IN_PROGRESS);

        order.markCompleted();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void markCompleted_whenAlreadyCompleted_keepsStatus() {
        Order order = Order.reconstruct(5L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.COMPLETED);

        order.markCompleted();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void markInvoiced_whenCompleted_setsInvoiced() {
        Order order = Order.reconstruct(6L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.COMPLETED);

        order.markInvoiced();

        assertEquals(OrderStatus.INVOICED, order.getStatus());
    }

    @Test
    void markInvoiced_whenNotCompleted_keepsStatus() {
        Order order = Order.reconstruct(7L, "A1", "Cliente Test", "test@test.com", List.of(new Product("Tea", ProductType.DRINK, 5)), OrderStatus.IN_PROGRESS);

        order.markInvoiced();

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }
}
