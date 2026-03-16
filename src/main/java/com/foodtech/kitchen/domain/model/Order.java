package com.foodtech.kitchen.domain.model;

import java.util.ArrayList;
import java.util.List;


public class Order {

    private final Long id;
    private final String tableNumber;
    private final String customerName;
    private final String customerEmail;
    private final List<Product> products;
    private OrderStatus status;

    public Order(String tableNumber, String customerName, String customerEmail, List<Product> products) {
        validate(tableNumber, customerName, customerEmail, products);
        this.id = null;
        this.tableNumber = tableNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.products = new ArrayList<>(products);
        this.status = OrderStatus.CREATED;
    }

    private Order(Long id, String tableNumber, String customerName, String customerEmail, List<Product> products, OrderStatus status) {
        validate(tableNumber, customerName, customerEmail, products);
        validateStatus(status);
        this.id = id;
        this.tableNumber = tableNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.products = new ArrayList<>(products);
        this.status = status;
    }

    public static Order reconstruct(Long id, String tableNumber, String customerName, String customerEmail, List<Product> products) {
        validateId(id);
        return new Order(id, tableNumber, customerName, customerEmail, products, OrderStatus.CREATED);
    }

    public static Order reconstruct(Long id, String tableNumber, String customerName, String customerEmail, List<Product> products, OrderStatus status) {
        validateId(id);
        return new Order(id, tableNumber, customerName, customerEmail, products, defaultStatus(status));
    }

    private void validate(String tableNumber, String customerName, String customerEmail, List<Product> products) {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
    }

    private static void validateId(Long id) {
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

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
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

    public void markInvoiced() {
        if (this.status == OrderStatus.COMPLETED) {
            this.status = OrderStatus.INVOICED;
        }
    }
}
