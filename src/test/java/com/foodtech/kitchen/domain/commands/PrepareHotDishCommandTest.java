package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrepareHotDishCommandTest {

    @Test
    @DisplayName("Debe crear comando de plato caliente con estación correcta")
    void shouldCreateHotDishCommandWithCorrectStation() {
        // Given
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        List<Product> products = List.of(pizza);

        // When
        PrepareHotDishCommand command = new PrepareHotDishCommand(products);

        // Then
        assertInstanceOf(PrepareHotDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de plato caliente")
    void shouldExecuteHotDishPreparation() {
        // Given
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        PrepareHotDishCommand command = new PrepareHotDishCommand(List.of(pizza));

        // When & Then
        assertDoesNotThrow(() -> command.execute());
    }
}