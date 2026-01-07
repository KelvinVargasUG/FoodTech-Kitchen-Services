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
        assertEquals(Station.BARRA, tasks.get(0).getStation(), "La bebida debe ir a BARRA");
        assertEquals(1, tasks.get(0).getProducts().size(), "La tarea debe contener un producto");
    }
}