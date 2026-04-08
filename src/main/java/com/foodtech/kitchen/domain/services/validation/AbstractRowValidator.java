package com.foodtech.kitchen.domain.services.validation;

public abstract class AbstractRowValidator implements RowValidator {

    private RowValidator next;

    @Override
    public RowValidator andThen(RowValidator next) {
        this.next = next;
        return next;
    }

    protected String passToNext(String[] row, int rowNumber) {
        return next != null ? next.validate(row, rowNumber) : null;
    }
}
