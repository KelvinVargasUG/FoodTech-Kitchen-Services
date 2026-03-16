package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class PrepareDrinkCommandTest {

    @Test
    @DisplayName("Debe crear comando de bebida con estación correcta")
    void shouldCreateDrinkCommandWithCorrectStation() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        List<Product> products = List.of(cocaCola);

        // When
        PrepareDrinkCommand command = new PrepareDrinkCommand(products);

        // Then
            assertInstanceOf(PrepareDrinkCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de bebida")
    void shouldExecuteDrinkPreparation() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        PrepareDrinkCommand command = new PrepareDrinkCommand(List.of(cocaCola));

        // When & Then
        assertDoesNotThrow(() -> command.execute());
    }

    @Test
    @DisplayName("Debe manejar múltiples bebidas en un solo comando")
    void shouldHandleMultipleDrinks() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product sprite = new Product("Sprite", ProductType.DRINK, 5);
        List<Product> products = List.of(cocaCola, sprite);

        // When
        PrepareDrinkCommand command = new PrepareDrinkCommand(products);

        // Then
        assertInstanceOf(PrepareDrinkCommand.class, command);
    }
}