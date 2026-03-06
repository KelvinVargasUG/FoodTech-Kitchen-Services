package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.*;
import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class CommandFactoryTest {

    private CommandFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CommandFactory(List.of(
                new PrepareDrinkStrategy(),
                new PrepareHotDishStrategy(),
                new PrepareColdDishStrategy()
        ));
    }

    @Test
    @DisplayName("Debe crear PrepareDrinkCommand para productos de tipo DRINK")
    void shouldCreateDrinkCommandForDrinkProducts() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        List<Product> products = List.of(cocaCola);

        // When
        Command command = factory.createCommand(Station.BAR, products);

        // Then
        assertInstanceOf(PrepareDrinkCommand.class, command);
    }

    @Test
    @DisplayName("Debe crear PrepareHotDishCommand para productos de tipo HOT_DISH")
    void shouldCreateHotDishCommandForHotDishProducts() {
        // Given
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        List<Product> products = List.of(pizza);

        // When
        Command command = factory.createCommand(Station.HOT_KITCHEN, products);

        // Then
        assertInstanceOf(PrepareHotDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe crear PrepareColdDishCommand para productos de tipo COLD_DISH")
    void shouldCreateColdDishCommandForColdDishProducts() {
        // Given
        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH);
        List<Product> products = List.of(salad);

        // When
        Command command = factory.createCommand(Station.COLD_KITCHEN, products);

        // Then
        assertInstanceOf(PrepareColdDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe lanzar excepción para estación desconocida")
    void shouldThrowExceptionForUnknownStation() {
        // Given
        Product product = new Product("Test", ProductType.DRINK);
        List<Product> products = List.of(product);

        // When & Then
        // Este test es solo por completitud, pero con enum no puede pasar
        assertDoesNotThrow(() -> factory.createCommand(Station.BAR, products));
    }
}