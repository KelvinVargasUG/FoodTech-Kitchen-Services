package com.foodtech.kitchen.domain.model;

import java.util.ArrayList;
import java.util.List;


public class Order {

    private final Long id;
    private final String tableNumber;
    private final List<Product> products;

    public Order(String tableNumber, List<Product> products) {
        validate(tableNumber, products);
        this.id = null;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
    }

    private Order(Long id, String tableNumber, List<Product> products) {
        validate(tableNumber, products);
        this.id = id;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
    }

    public static Order reconstruct(Long id, String tableNumber, List<Product> products) {
        validateId(id);
        return new Order(id, tableNumber, products);
    }

    private void validate(String tableNumber, List<Product> products) {

        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
    }

    private static void validateId(Long id){
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null when reconstructing Order");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }
}
