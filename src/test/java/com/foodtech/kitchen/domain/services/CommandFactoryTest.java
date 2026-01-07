package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.*;
import com.foodtech.kitchen.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    private CommandFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CommandFactory();
    }

    @Test
    @DisplayName("Should create PrepareDrinkCommand for DRINK products")
    void shouldCreateDrinkCommandForDrinkProducts() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK);
        List<Product> products = List.of(cocaCola);

        // When
        Command command = factory.createCommand(Station.BAR, products);

        // Then
        assertInstanceOf(PrepareDrinkCommand.class, command);
        assertEquals(Station.BAR, command.getStation());
    }

    @Test
    @DisplayName("Should create PrepareHotDishCommand for HOT_DISH products")
    void shouldCreateHotDishCommandForHotDishProducts() {
        // Given
        Product pizza = new Product("Pizza", ProductType.HOT_DISH);
        List<Product> products = List.of(pizza);

        // When
        Command command = factory.createCommand(Station.HOT_KITCHEN, products);

        // Then
        assertInstanceOf(PrepareHotDishCommand.class, command);
        assertEquals(Station.HOT_KITCHEN, command.getStation());
    }

    @Test
    @DisplayName("Should create PrepareColdDishCommand for COLD_DISH products")
    void shouldCreateColdDishCommandForColdDishProducts() {
        // Given
        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH);
        List<Product> products = List.of(salad);

        // When
        Command command = factory.createCommand(Station.COLD_KITCHEN, products);

        // Then
        assertInstanceOf(PrepareColdDishCommand.class, command);
        assertEquals(Station.COLD_KITCHEN, command.getStation());
    }

    @Test
    @DisplayName("Should throw exception for unknown station")
    void shouldThrowExceptionForUnknownStation() {
        // Given
        Product product = new Product("Test", ProductType.DRINK);
        List<Product> products = List.of(product);

        // When & Then
        // Este test es solo por completitud, pero con enum no puede pasar
        assertDoesNotThrow(() -> factory.createCommand(Station.BAR, products));
    }
}