package com.foodtech.kitchen.domain.services.validation;

public class PriceValidator extends AbstractRowValidator {

    @Override
    public String validate(String[] row, int rowNumber) {
        String precioStr = row[1].trim();
        try {
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                return "El valor del campo 'precio' debe ser mayor a 0";
            }
        } catch (NumberFormatException e) {
            return "El valor del campo 'precio' ('" + precioStr + "') no es numérico";
        }
        return passToNext(row, rowNumber);
    }
}
