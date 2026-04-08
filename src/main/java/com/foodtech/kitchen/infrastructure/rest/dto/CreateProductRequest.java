package com.foodtech.kitchen.infrastructure.rest.dto;

import com.foodtech.kitchen.domain.model.ProductType;

public class CreateProductRequest {

    private String name;
    private ProductType type;
    private String category;
    private int price;

    public CreateProductRequest() {}

    public CreateProductRequest(String name, ProductType type, String category, int price) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.price = price;
    }

    public String getName() { return name; }
    public ProductType getType() { return type; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
}
