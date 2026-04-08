package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class TaskDecomposerTest {

    private TaskDecomposer decomposer;
    private OrderValidator orderValidator;
    private TaskFactory taskFactory;

    @BeforeEach
    void setUp() {
        orderValidator = new OrderValidator();
        taskFactory = new TaskFactory();
        decomposer = new TaskDecomposer(orderValidator, taskFactory);
    }

    @Test
    @DisplayName("Debe crear una tarea para un pedido con una sola bebida")
    void shouldCreateOneTaskForSingleDrink() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Order order = new Order("A1", "Cliente Test", "test@test.com", List.of(cocaCola));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(1, tasks.size(), "Debe crear exactamente una tarea");
        assertEquals(Station.BAR, tasks.get(0).getStation(), "La bebida debe ir a BARRA");
        assertEquals(1, tasks.get(0).getProducts().size(), "La tarea debe contener un producto");
    }

    @Test
    @DisplayName("Debe crear una tarea para un pedido con un solo plato caliente")
    void shouldCreateOneTaskForSingleHotDish() {

        Product pizza = new Product("Pizza Margarita", ProductType.HOT_DISH, 5);
        Order order = new Order("B2", "Cliente Test", "test@test.com", List.of(pizza));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(1, tasks.size());
        assertEquals(Station.HOT_KITCHEN, tasks.get(0).getStation());
    }

    @Test
    @DisplayName("Debe crear una tarea para un pedido con un solo plato frío")
    void shouldCreateOneTaskForSingleColdDish() {

        Product salad = new Product("Caesar Salad", ProductType.COLD_DISH, 5);
        Order order = new Order("C3", "Cliente Test", "test@test.com", List.of(salad));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(1, tasks.size());
        assertEquals(Station.COLD_KITCHEN, tasks.get(0).getStation());
    }

    @Test
    @DisplayName("Debe crear tareas separadas para distintos tipos de producto")
    void shouldCreateSeparateTasksForMixedOrder() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        Order order = new Order("D4", "Cliente Test", "test@test.com", List.of(cocaCola, pizza));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(2, tasks.size(), "Debe crear dos tareas separadas");

        boolean hasDrinkTask = tasks.stream()
                .anyMatch(task -> task.getStation() == Station.BAR);
        boolean hasHotDishTask = tasks.stream()
                .anyMatch(task -> task.getStation() == Station.HOT_KITCHEN);

        assertTrue(hasDrinkTask, "Debe existir una tarea para BAR");
        assertTrue(hasHotDishTask, "Debe existir una tarea para la cocina caliente");
    }

    @Test
    @DisplayName("Debe agrupar productos del mismo tipo en una sola tarea")
    void shouldGroupProductsOfSameTypeInSingleTask() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product sprite = new Product("Sprite", ProductType.DRINK, 5);
        Order order = new Order("E5", "Cliente Test", "test@test.com", List.of(cocaCola, sprite));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(1, tasks.size(), "Debe crear solo UNA tarea para la misma estación");
        assertEquals(2, tasks.get(0).getProducts().size(), "La tarea debe contener ambos productos");
        assertEquals(Station.BAR, tasks.get(0).getStation());
    }

    @Test
    @DisplayName("Debe rechazar un pedido sin productos")
    void shouldRejectEmptyOrder() {

        assertThrows(
            IllegalArgumentException.class,
            () -> new Order("F6", "Cliente Test", "test@test.com", List.of()),
            "Debe lanzar excepción para pedido vacío");
    }

    @Test
    @DisplayName("Debe rechazar un pedido nulo")
    void shouldRejectNullOrder() {

        assertThrows(
                IllegalArgumentException.class,
                () -> decomposer.decompose(null),
                "Debe lanzar excepción para pedido nulo");
    }

    @Test
    @DisplayName("Debe rechazar un pedido con número de mesa nulo")
    void shouldRejectNullTableNumber() {

        Product product = new Product("Coca Cola", ProductType.DRINK, 5);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(null, "Cliente Test", "test@test.com", List.of(product)),
                "Debe lanzar excepción para número de mesa nulo");
    }

    @Test
    @DisplayName("Debe crear tres tareas para un pedido con todos los tipos de producto")
    void shouldCreateThreeTasksForAllProductTypes() {

        Product drink = new Product("Coca Cola", ProductType.DRINK, 5);
        Product hotDish = new Product("Pizza", ProductType.HOT_DISH, 5);
        Product coldDish = new Product("Caesar Salad", ProductType.COLD_DISH, 5);
        Order order = new Order("G7", "Cliente Test", "test@test.com", List.of(drink, hotDish, coldDish));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(3, tasks.size(), "Debe crear tres tareas");

        long barTasks = tasks.stream()
                .filter(task -> task.getStation() == Station.BAR)
                .count();
        long hotKitchenTasks = tasks.stream()
                .filter(task -> task.getStation() == Station.HOT_KITCHEN)
                .count();
        long coldKitchenTasks = tasks.stream()
                .filter(task -> task.getStation() == Station.COLD_KITCHEN)
                .count();

        assertEquals(1, barTasks, "Debe tener una tarea para BAR");
        assertEquals(1, hotKitchenTasks, "Debe tener una tarea para HOT_KITCHEN");
        assertEquals(1, coldKitchenTasks, "Debe tener una tarea para COLD_KITCHEN");
    }

    @Test
    @DisplayName("Debe crear comandos para cada tarea")
    void shouldCreateCommandsForEachTask() {

        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product pizza = new Product("Pizza", ProductType.HOT_DISH, 5);
        Order order = new Order("H8", "Cliente Test", "test@test.com", List.of(cocaCola, pizza));

        CommandFactory commandFactory = new CommandFactory(List.of(
            new PrepareDrinkStrategy(),
            new PrepareHotDishStrategy(),
            new PrepareColdDishStrategy()
        ));

        List<Task> tasks = decomposer.decompose(order);

        assertEquals(2, tasks.size());

        for (Task task : tasks) {
            Command command = commandFactory.createCommand(task.getStation(), task.getProducts());
            assertNotNull(command);
            assertInstanceOf(Command.class, command);
        }
    }

}
