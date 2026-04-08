package com.foodtech.kitchen.application.usecases.dto;

import com.foodtech.kitchen.domain.model.ProductType;

public class CreateProductCommand {

    private final String name;
    private final ProductType type;
    private final String category;
    private final int price;

    public CreateProductCommand(String name, ProductType type, String category, int price) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }
}
