package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskDecomposerTest {

    private TaskDecomposer decomposer;

    @BeforeEach
    void setUp() {
        decomposer = new TaskDecomposer();
    }

    @Test
    @DisplayName("Debe crear una tarea para un pedido con una sola bebida")
    void shouldCreateOneTaskForSingleDrink() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Order order = new Order("A1", List.of(cocaCola));

        // When
        List<Task> tasks = decomposer.decompose(order);

        // Then
        assertEquals(1, tasks.size(), "Debe crear exactamente una tarea");
        assertEquals(Station.BAR, tasks.get(0).getStation(), "La bebida debe ir a BARRA");
        assertEquals(1, tasks.get(0).getProducts().size(), "La tarea debe contener un producto");
    }

    @Test
    @DisplayName("Should create one task for order with single hot dish")
    void shouldCreateOneTaskForSingleHotDish() {
        // Given
        Product pizza = new Product("Pizza Margarita", ProductType.HOT_DISH);
        Order order = new Order("B2", List.of(pizza));

        // When
        List<Task> tasks = decomposer.decompose(order);

        // Then
        assertEquals(1, tasks.size());
        assertEquals(Station.HOT_KITCHEN, tasks.get(0).getStation());
    }

    @Test
    @DisplayName("Should create one task for order with single cold dish")
    void shouldCreateOneTaskForSingleColdDish() {
        // Given
        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH);
        Order order = new Order("C3", List.of(salad));

        // When
        List<Task> tasks = decomposer.decompose(order);

        // Then
        assertEquals(1, tasks.size());
        assertEquals(Station.COLD_KITCHEN, tasks.get(0).getStation());
    }

    @Test
    @DisplayName("Should create separate tasks for different product types")
    void shouldCreateSeparateTasksForMixedOrder() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        Order order = new Order("D4", List.of(cocaCola, pizza));

        // When
        List<Task> tasks = decomposer.decompose(order);

        // Then
        assertEquals(2, tasks.size(), "Should create two separate tasks");

        boolean hasDrinkTask = tasks.stream()
                .anyMatch(task -> task.getStation() == Station.BAR);
        boolean hasHotDishTask = tasks.stream()
                .anyMatch(task -> task.getStation() == Station.HOT_KITCHEN);

        assertTrue(hasDrinkTask, "Should have task for BARRA");
        assertTrue(hasHotDishTask, "Should have task for COCINA_CALIENTE");
    }

}