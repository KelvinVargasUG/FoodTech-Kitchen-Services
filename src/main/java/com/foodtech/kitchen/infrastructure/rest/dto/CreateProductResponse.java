package com.foodtech.kitchen.infrastructure.rest.dto;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;

import java.util.UUID;

public class CreateProductResponse {

    private final UUID id;
    private final String name;
    private final ProductType type;
    private final String category;
    private final int price;
    private final ProductStatus status;

    public CreateProductResponse(UUID id, String name, ProductType type,
            String category, int price, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.price = price;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public ProductType getType() { return type; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public ProductStatus getStatus() { return status; }
}
