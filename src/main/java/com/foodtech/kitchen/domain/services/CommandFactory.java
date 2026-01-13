package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.*;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Station;

import java.util.List;

public class CommandFactory {

    public Command createCommand(Station station, List<Product> products) {
        return switch (station) {
            case BAR -> new PrepareDrinkCommand(products);
            case HOT_KITCHEN -> new PrepareHotDishCommand(products);
            case COLD_KITCHEN -> new PrepareColdDishCommand(products);
        };
    }

    
}