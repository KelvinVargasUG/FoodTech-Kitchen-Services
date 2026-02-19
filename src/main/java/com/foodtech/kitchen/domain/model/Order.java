package com.foodtech.kitchen.domain.model;

import java.util.ArrayList;
import java.util.List;


public class Order {

    private final Long id;
    private final String tableNumber;
    private final List<Product> products;
    private OrderStatus status;

    public Order(String tableNumber, List<Product> products) {
        validate(tableNumber, products);
        this.id = null;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
        this.status = OrderStatus.CREATED;
    }

    private Order(Long id, String tableNumber, List<Product> products, OrderStatus status) {
        validate(tableNumber, products);
        validateStatus(status);
        this.id = id;
        this.tableNumber = tableNumber;
        this.products = new ArrayList<>(products);
        this.status = status;
    }

    public static Order reconstruct(Long id, String tableNumber, List<Product> products) {
        validateId(id);
        return new Order(id, tableNumber, products, OrderStatus.CREATED);
    }

    public static Order reconstruct(Long id, String tableNumber, List<Product> products, OrderStatus status) {
        validateId(id);
        return new Order(id, tableNumber, products, defaultStatus(status));
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

    private static void validateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
    }

    private static OrderStatus defaultStatus(OrderStatus status) {
        return status != null ? status : OrderStatus.CREATED;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void markInProgress() {
        if (this.status == OrderStatus.CREATED) {
            this.status = OrderStatus.IN_PROGRESS;
        }
    }

    public void markCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            this.status = OrderStatus.COMPLETED;
        }
    }
}
