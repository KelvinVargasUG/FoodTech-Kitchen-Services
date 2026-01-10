package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;


import java.util.ArrayList;
import java.util.List;

public class PrepareDrinkCommand implements Command {
    private final List<Product> products;

    public PrepareDrinkCommand(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public void execute() {
        System.out.println("Preparing drinks at BAR:");
        for (Product product : products) {
            System.out.println("  - " + product.getName());
        }
    }

}