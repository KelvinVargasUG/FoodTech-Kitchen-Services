package com.foodtech.kitchen.domain.model;

//HUMAN REVIEW: Agregué campo station a ProductType para eliminar violación OCP.
//Ahora cada ProductType conoce su Station, eliminando necesidad de ProductStationMapper.
//Cumple OCP: agregar nuevo tipo no requiere modificar otras clases.
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
