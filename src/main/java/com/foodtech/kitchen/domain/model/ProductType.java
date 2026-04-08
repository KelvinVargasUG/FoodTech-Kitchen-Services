package com.foodtech.kitchen.domain.model;

public enum ProductType {
    DRINK(Station.BAR),
    HOT_DISH(Station.HOT_KITCHEN),
    COLD_DISH(Station.COLD_KITCHEN);

    private final Station station;

    ProductType(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }
}
