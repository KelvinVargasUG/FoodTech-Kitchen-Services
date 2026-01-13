package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PrepareColdDishCommand implements Command {
    private static final int SECONDS_PER_COLD_DISH = 5;

    private final List<Product> products;

    public PrepareColdDishCommand(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public void execute() {
        System.out.println("\n[COLD_KITCHEN] 🥗 Starting preparation of " + products.size() + " cold dish(es)");

        int totalTime = 0;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("[COLD_KITCHEN] Preparing dish " + (i + 1) + "/" + products.size() + ": " + product.getName());

            simulatePreparation(SECONDS_PER_COLD_DISH);
            totalTime += SECONDS_PER_COLD_DISH;

            System.out.println("[COLD_KITCHEN] ✓ " + product.getName() + " ready!");
        }

        System.out.println("[COLD_KITCHEN] ✅ All cold dishes completed in " + totalTime + " seconds\n");
    }

    private void simulatePreparation(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cold dish preparation interrupted", e);
        }
    }
}