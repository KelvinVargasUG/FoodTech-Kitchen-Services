package com.foodtech.kitchen.application.usecases.dto;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;

public class UpdateProductCommand {

    private final String name;
    private final String description;
    private final ProductType type;
    private final String category;
    private final int price;
    private final ProductStatus status;

    public UpdateProductCommand(String name, String description,
            ProductType type, String category, int price, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.price = price;
        this.status = status;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProductType getType() { return type; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public ProductStatus getStatus() { return status; }
}
