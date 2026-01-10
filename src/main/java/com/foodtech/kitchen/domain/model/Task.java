package com.foodtech.kitchen.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private final Station station;
    private final String tableNumber;  // ✅ AGREGADO
    private final List<Product> products;

    public Task(Station station, String tableNumber, List<Product> products) {
        validate(station, tableNumber, products);
        this.station = station;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
    }

    private void validate(Station station, String tableNumber, List<Product> products) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
    }

    public Station getStation() {
        return station;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }
}