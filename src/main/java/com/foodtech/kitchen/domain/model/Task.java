package com.foodtech.kitchen.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private final Station station;
    private final String tableNumber;
    private final List<Product> products;
    private final LocalDateTime createdAt;

    public Task(Station station, String tableNumber, List<Product> products, LocalDateTime createdAt) {
        validate(station, tableNumber, products, createdAt);
        this.station = station;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
        this.createdAt = createdAt;
    }

    private void validate(Station station, String tableNumber, List<Product> products, LocalDateTime createdAt) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at timestamp cannot be null");
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}