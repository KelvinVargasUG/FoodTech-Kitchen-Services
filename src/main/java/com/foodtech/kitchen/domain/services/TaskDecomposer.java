package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.model.*;

import java.util.*;

public class TaskDecomposer {
    
    private final CommandFactory commandFactory;

    // Constructor sin parámetros para compatibilidad con tests anteriores
    public TaskDecomposer() {
        this.commandFactory = null;
    }

    // Constructor con CommandFactory
    public TaskDecomposer(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public List<Task> decompose(Order order) {
        validateOrder(order);
        
        Map<Station, List<Product>> productsByStation = groupProductsByStation(order);
        
        return createTasksFromGroupedProducts(productsByStation);
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (order.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }
    }

    private Map<Station, List<Product>> groupProductsByStation(Order order) {
        Map<Station, List<Product>> productsByStation = new HashMap<>();
        
        for (Product product : order.getProducts()) {
            Station station = mapProductTypeToStation(product.getType());
            productsByStation
                .computeIfAbsent(station, k -> new ArrayList<>())
                .add(product);
        }
        
        return productsByStation;
    }

    private List<Task> createTasksFromGroupedProducts(Map<Station, List<Product>> productsByStation) {
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<Station, List<Product>> entry : productsByStation.entrySet()) {
            tasks.add(new Task(entry.getKey(), entry.getValue()));
        }
        return tasks;
    }

    private Station mapProductTypeToStation(ProductType type) {
        return switch (type) {
            case DRINK -> Station.BAR;
            case HOT_DISH -> Station.HOT_KITCHEN;
            case COLD_DISH -> Station.COLD_KITCHEN;
        };
    }
}