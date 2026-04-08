package com.foodtech.kitchen.domain.services.validation;

public interface RowValidator {

    String validate(String[] row, int rowNumber);

    RowValidator andThen(RowValidator next);
}
