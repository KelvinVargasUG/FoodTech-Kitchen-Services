package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.Product;

import java.util.List;

public interface GetActiveProductsPort {
    List<Product> execute();
    List<Product> executeByCategory(String category);
    List<Product> executeByName(String name);
}
