package com.foodtech.kitchen.domain.services.validation;

public class StationValidator extends AbstractRowValidator {

    private static final java.util.Set<String> VALID_STATIONS =
            java.util.Set.of("BAR", "HOT_KITCHEN", "COLD_KITCHEN");

    @Override
    public String validate(String[] row, int rowNumber) {
        String categoria = row[2].trim();
        if (categoria.isEmpty()) {
            return "El campo 'categoria' es obligatorio";
        }

        String estacion = row[3].trim();
        if (!VALID_STATIONS.contains(estacion)) {
            return "El valor de la estación '" + estacion
                    + "' no es válido. Debe ser BAR, HOT_KITCHEN o COLD_KITCHEN";
        }
        return passToNext(row, rowNumber);
    }
}
