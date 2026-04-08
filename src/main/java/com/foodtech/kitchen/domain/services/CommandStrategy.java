package com.foodtech.kitchen.domain.services;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.Station;

import java.util.List;

public interface CommandStrategy {
    boolean supports(Station station);

    Command createCommand(List<Product> products);
}
