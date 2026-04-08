package com.foodtech.kitchen.domain.services.validation;

public class StatusValidator extends AbstractRowValidator {

    private static final java.util.Set<String> VALID_STATUSES =
            java.util.Set.of("Activo", "Inactivo", "");

    @Override
    public String validate(String[] row, int rowNumber) {
        if (row.length > 5) {
            String estado = row[5].trim();
            if (!VALID_STATUSES.contains(estado)) {
                return "El valor del campo 'estado' ('" + estado
                        + "') no es válido. Debe ser Activo o Inactivo";
            }
        }
        return passToNext(row, rowNumber);
    }
}
