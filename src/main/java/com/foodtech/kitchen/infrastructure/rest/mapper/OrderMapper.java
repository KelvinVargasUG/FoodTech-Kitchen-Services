package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderMapper {

    public static Order toDomain(CreateOrderRequest request) {
        List<Product> products = request.products().stream()
            .map(OrderMapper::mapProduct)
            .collect(Collectors.toList());
        
        return new Order(request.tableNumber(), products);
    }

    private static Product mapProduct(Map<String, String> productMap) {
        String name = productMap.get("name");
        String typeStr = productMap.get("type");
        
        try {
            ProductType type = ProductType.valueOf(typeStr);
            return new Product(name, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product type: " + typeStr);
        }
    }
}