package com.foodtech.kitchen.domain.model;

public class Product {

    private final String name;
    private final ProductType type;
    private final int price;

    public Product(String name, ProductType type, int price) {
        validate(name, type, price);
        this.name = name;
        this.type = type;
        this.price = price;
    }

    private void validate(String name, ProductType type, int price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }
}

