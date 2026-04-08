package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.commands.PrepareColdDishCommand;
import com.foodtech.kitchen.domain.commands.PrepareDrinkCommand;
import com.foodtech.kitchen.domain.commands.PrepareHotDishCommand;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        List<Product> products = List.of(cocaCola);

        Command command = factory.createCommand(Station.BAR, products);

        assertInstanceOf(PrepareDrinkCommand.class, command);
    }

    @Test
    @DisplayName("Debe crear PrepareHotDishCommand para productos de tipo HOT_DISH")
    void shouldCreateHotDishCommandForHotDishProducts() {

        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        List<Product> products = List.of(pizza);

        Command command = factory.createCommand(Station.HOT_KITCHEN, products);

        assertInstanceOf(PrepareHotDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe crear PrepareColdDishCommand para productos de tipo COLD_DISH")
    void shouldCreateColdDishCommandForColdDishProducts() {

        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH, 5);
        List<Product> products = List.of(salad);

        Command command = factory.createCommand(Station.COLD_KITCHEN, products);

        assertInstanceOf(PrepareColdDishCommand.class, command);
    }

    @Test
    @DisplayName("Debe lanzar excepción para estación desconocida")
    void shouldThrowExceptionForUnknownStation() {

        Product product = new Product("Test", ProductType.DRINK, 5);
        List<Product> products = List.of(product);

        assertDoesNotThrow(() -> factory.createCommand(Station.BAR, products));
    }
}
