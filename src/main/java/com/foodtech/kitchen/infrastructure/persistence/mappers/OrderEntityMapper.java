package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    private final ProductEntityMapper productEntityMapper;

    public OrderEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public OrderEntity toEntity(Order order) {
        List<ProductEntity> products = order.getProducts().stream()
                .map(productEntityMapper::toProductEntity)
                .collect(Collectors.toList());

        return OrderEntity.builder()
                .id(order.getId())
                .tableNumber(order.getTableNumber())
                .products(products)
                .build();
    }

    public Order toDomain(OrderEntity entity) {
        List<Product> products = entity.getProducts().stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());


        return Order.reconstruct(entity.getId(), entity.getTableNumber(), products);
    }
}