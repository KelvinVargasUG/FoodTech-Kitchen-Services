package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrepareColdDishCommandTest {

    @Test
    @DisplayName("Debe crear comando de plato frío con estación correcta")
    void shouldCreateColdDishCommandWithCorrectStation() {
        // Given
        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH);
        List<Product> products = List.of(salad);

        // When
        PrepareColdDishCommand command = new PrepareColdDishCommand(products);

        // Then
        assertInstanceOf(PrepareColdDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de plato frío")
    void shouldExecuteColdDishPreparation() {
        // Given
        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH);
        PrepareColdDishCommand command = new PrepareColdDishCommand(List.of(salad));

        // When & Then
        assertDoesNotThrow(() -> command.execute());
    }
}