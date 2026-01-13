package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;

import java.util.List;
import java.util.stream.Collectors;

//HUMAN REVIEW: Eliminada "inappropriate intimacy" con Map. Ahora usa ProductRequest tipado.
public class OrderMapper {

    private OrderMapper() {
    }

    public static Order toDomain(CreateOrderRequest request) {

        List<Product> products = request.products().stream()
            .map(ProductMapper::mapProduct)
            .collect(Collectors.toList());
        
        return new Order(request.tableNumber(), products);
    }

}