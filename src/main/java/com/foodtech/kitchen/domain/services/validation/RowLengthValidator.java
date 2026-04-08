package com.foodtech.kitchen.domain.services.validation;

public class RowLengthValidator extends AbstractRowValidator {

    @Override
    public String validate(String[] row, int rowNumber) {
        if (row.length < 4) {
            return "Fila " + rowNumber + ": columnas insuficientes";
        }
        return passToNext(row, rowNumber);
    }
}
