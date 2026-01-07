package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDecomposer {

    public List<Task> decompose(Order order) {
        Map<Station, List<Product>> productsByStation = new HashMap<>();

        for (Product product : order.getProducts()) {
            Station station = mapProductTypeToStation(product.getType());
            productsByStation
                .computeIfAbsent(station, k -> new ArrayList<>())
                .add(product);
        }

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
