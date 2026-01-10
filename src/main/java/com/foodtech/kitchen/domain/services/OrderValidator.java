package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.model.Order;

//HUMAN REVIEW: Extraje la responsabilidad de validación a su propia clase.
//Cumple SRP: solo valida órdenes, nada más.
public class OrderValidator {

    public void validate(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (order.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }
    }
}
