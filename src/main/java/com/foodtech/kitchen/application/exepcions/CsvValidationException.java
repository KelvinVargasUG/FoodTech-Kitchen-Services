package com.foodtech.kitchen.application.exepcions;

public class CsvValidationException extends RuntimeException {
    public CsvValidationException(String message) {
        super(message);
    }
}
