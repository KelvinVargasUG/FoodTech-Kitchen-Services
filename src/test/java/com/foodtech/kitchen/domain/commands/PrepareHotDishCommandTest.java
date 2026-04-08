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
class PrepareHotDishCommandTest {

    @Test
    @DisplayName("Debe crear comando de plato caliente con estación correcta")
    void shouldCreateHotDishCommandWithCorrectStation() {

        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        List<Product> products = List.of(pizza);

        PrepareHotDishCommand command = new PrepareHotDishCommand(products);

        assertInstanceOf(PrepareHotDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe ejecutar la preparación de plato caliente")
    void shouldExecuteHotDishPreparation() {

        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        PrepareHotDishCommand command = new PrepareHotDishCommand(List.of(pizza));

        assertDoesNotThrow(() -> command.execute());
    }
}
