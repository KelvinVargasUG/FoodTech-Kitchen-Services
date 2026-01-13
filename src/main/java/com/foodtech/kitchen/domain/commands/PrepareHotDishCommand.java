package com.foodtech.kitchen.domain.commands;

import com.foodtech.kitchen.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PrepareHotDishCommand implements Command {
    private static final int SECONDS_PER_HOT_DISH = 7;

    private final List<Product> products;

    public PrepareHotDishCommand(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public void execute() {
        System.out.println("\n[HOT_KITCHEN] 🔥 Starting preparation of " + products.size() + " hot dish(es)");

        int totalTime = 0;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("[HOT_KITCHEN] Cooking dish " + (i + 1) + "/" + products.size() + ": " + product.getName());

            simulatePreparation(SECONDS_PER_HOT_DISH);
            totalTime += SECONDS_PER_HOT_DISH;

            System.out.println("[HOT_KITCHEN] ✓ " + product.getName() + " ready!");
        }

        System.out.println("[HOT_KITCHEN] ✅ All hot dishes completed in " + totalTime + " seconds\n");
    }

    private void simulatePreparation(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hot dish preparation interrupted", e);
        }
    }
}