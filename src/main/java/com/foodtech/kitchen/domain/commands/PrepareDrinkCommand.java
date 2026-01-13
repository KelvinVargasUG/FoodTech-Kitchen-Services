package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PrepareDrinkCommand implements Command {
    private static final int SECONDS_PER_DRINK = 3;

    private final List<Product> products;

    public PrepareDrinkCommand(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public void execute() {
        System.out.println("\n[BAR] 🍹 Starting preparation of " + products.size() + " drink(s)");

        int totalTime = 0;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("[BAR] Preparing drink " + (i + 1) + "/" + products.size() + ": " + product.getName());

            simulatePreparation(SECONDS_PER_DRINK);
            totalTime += SECONDS_PER_DRINK;

            System.out.println("[BAR] ✓ " + product.getName() + " ready!");
        }

        System.out.println("[BAR] ✅ All drinks completed in " + totalTime + " seconds\n");
    }

    private void simulatePreparation(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Drink preparation interrupted", e);
        }
    }
}