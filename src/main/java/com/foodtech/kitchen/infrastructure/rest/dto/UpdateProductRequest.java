package com.foodtech.kitchen.infrastructure.rest.dto;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;

public class UpdateProductRequest {

    private String name;
    private String description;
    private ProductType type;
    private String category;
    private int price;
    private ProductStatus status;

    public UpdateProductRequest() {}

    public UpdateProductRequest(String name, String description,
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
