package com.foodtech.kitchen.domain.model;

public class Product {

    private final String name;
    private final ProductType type;

    public Product(String name, ProductType type) {
        validate(name, type);
        this.name = name;
        this.type = type;
    }

    private void validate(String name, ProductType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }
}

