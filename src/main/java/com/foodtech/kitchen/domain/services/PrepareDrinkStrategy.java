package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.commands.PrepareDrinkCommand;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Station;

import java.util.List;

public class PrepareDrinkStrategy implements CommandStrategy {

    @Override
    public boolean supports(Station station) {
        return station == Station.BAR;
    }

    @Override
    public Command createCommand(List<Product> products) {
        return new PrepareDrinkCommand(products);
    }
}
