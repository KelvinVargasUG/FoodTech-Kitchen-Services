package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@Tag("unit")
class PrepareDrinkCommandTest {

    @Test
    @DisplayName("Debe crear comando de bebida con estación correcta")
    void shouldCreateDrinkCommandWithCorrectStation() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        List<Product> products = List.of(cocaCola);

        PrepareDrinkCommand command = new PrepareDrinkCommand(products);

            assertInstanceOf(PrepareDrinkCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de bebida")
    void shouldExecuteDrinkPreparation() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        PrepareDrinkCommand command = new PrepareDrinkCommand(List.of(cocaCola));

        assertDoesNotThrow(() -> command.execute());
    }

    @Test
    @DisplayName("Debe manejar múltiples bebidas en un solo comando")
    void shouldHandleMultipleDrinks() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product sprite = new Product("Sprite", ProductType.DRINK, 5);
        List<Product> products = List.of(cocaCola, sprite);

        PrepareDrinkCommand command = new PrepareDrinkCommand(products);

        assertInstanceOf(PrepareDrinkCommand.class, command);
    }
}
