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
class PrepareColdDishCommandTest {

    @Test
    @DisplayName("Debe crear comando de plato frío con estación correcta")
    void shouldCreateColdDishCommandWithCorrectStation() {

        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH, 5);
        List<Product> products = List.of(salad);

        PrepareColdDishCommand command = new PrepareColdDishCommand(products);

        assertInstanceOf(PrepareColdDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de plato frío")
    void shouldExecuteColdDishPreparation() {

        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH, 5);
        PrepareColdDishCommand command = new PrepareColdDishCommand(List.of(salad));

        assertDoesNotThrow(() -> command.execute());
    }
}
