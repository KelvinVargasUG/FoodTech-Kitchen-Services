package com.foodtech.kitchen.domain.services.validation;

public class NameValidator extends AbstractRowValidator {

    @Override
    public String validate(String[] row, int rowNumber) {
        String nombre = row[0].trim();
        if (nombre.isEmpty()) {
            return "El campo 'nombre' es obligatorio";
        }
        return passToNext(row, rowNumber);
    }
}
