package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.rest.dto.ProductRequest;

public class ProductMapper {

    private ProductMapper() {
    }
    
    public static Product mapProduct(ProductRequest productRequest) {
        ProductType type = ProductType.valueOf(productRequest.type());
        return new Product(productRequest.name(), type);
    }
}
