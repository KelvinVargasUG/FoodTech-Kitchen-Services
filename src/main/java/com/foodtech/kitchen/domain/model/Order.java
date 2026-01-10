package com.foodtech.kitchen.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String tableNumber;
    private final List<Product> products;

    public Order(String tableNumber, List<Product> products) {
        validate(tableNumber, products);
        
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
    }

    private void validate(String tableNumber, List<Product> products) {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }
}
