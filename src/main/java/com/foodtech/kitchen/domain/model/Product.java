package com.foodtech.kitchen.domain.model;

import java.util.UUID;

public class Product {

    private final UUID id;
    private final String name;
    private final ProductType type;
    private final int price;
    private final ProductStatus status;
    private final String category;
    private final String description;

    public Product(String name, ProductType type, int price) {
        validateBase(name, type, price);
        this.id = null;
        this.name = name;
        this.type = type;
        this.price = price;
        this.status = null;
        this.category = null;
        this.description = null;
    }

    private Product(UUID id, String name, ProductType type, String category, int price, ProductStatus status,
            String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.status = status;
        this.category = category;
        this.description = description;
    }

    public static Product create(String name, ProductType type, String category, int price) {
        validateCreate(name, type, category, price);
        return new Product(UUID.randomUUID(), name, type, category, price, ProductStatus.ACTIVE, null);
    }

    public static Product create(String name, ProductType type, String category, int price, String description) {
        validateCreate(name, type, category, price);
        return new Product(UUID.randomUUID(), name, type, category, price, ProductStatus.ACTIVE, description);
    }

    public static Product reconstruct(UUID id, String name, ProductType type, String category, int price,
            ProductStatus status) {
        return new Product(id, name, type, category, price, status, null);
    }

    public static Product reconstruct(UUID id, String name, ProductType type, String category, int price,
            ProductStatus status, String description) {
        return new Product(id, name, type, category, price, status, description);
    }

    public Product update(String name, String description, ProductType type, String category, int price,
            ProductStatus status) {
        validateUpdate(name, type, category, price, status);
        return new Product(this.id, name, type, category, price, status, description);
    }

    public Product deactivate() {
        if (this.status == ProductStatus.INACTIVE) {
            throw new IllegalStateException("Product is already inactive");
        }
        return new Product(this.id, this.name, this.type, this.category, this.price, ProductStatus.INACTIVE,
                this.description);
    }

    public Product activate() {
        if (this.status == ProductStatus.ACTIVE) {
            throw new IllegalStateException("Product is already active");
        }
        return new Product(this.id, this.name, this.type, this.category, this.price, ProductStatus.ACTIVE,
                this.description);
    }

    private static void validateBase(String name, ProductType type, int price) {
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

    private static void validateCreate(String name, ProductType type, String category, int price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
    }

    private static void validateUpdate(String name, ProductType type, String category, int price,
            ProductStatus status) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (status == null) {
            throw new IllegalArgumentException("Product status cannot be null");
        }
    }

    public UUID getId() {
        return id;
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

    public ProductStatus getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}
