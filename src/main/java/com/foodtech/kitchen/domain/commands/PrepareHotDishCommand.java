package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Station;

import java.util.ArrayList;
import java.util.List;

public class PrepareHotDishCommand implements Command {
    private final List<Product> products;

    public PrepareHotDishCommand(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public void execute() {
        System.out.println("Preparing hot dishes at HOT_KITCHEN:");
        for (Product product : products) {
            System.out.println("  - " + product.getName());
        }
    }

    @Override
    public Station getStation() {
        return Station.HOT_KITCHEN;
    }

    @Override
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }
}