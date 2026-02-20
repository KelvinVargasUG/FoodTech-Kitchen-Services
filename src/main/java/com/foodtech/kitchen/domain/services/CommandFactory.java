package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Station;

import java.util.List;
import java.util.NoSuchElementException;

public class CommandFactory {

    private final List<CommandStrategy> strategies;

    public CommandFactory(List<CommandStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    public Command createCommand(Station station, List<Product> products) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No strategy for station: " + station))
                .createCommand(products);
    }

    
}