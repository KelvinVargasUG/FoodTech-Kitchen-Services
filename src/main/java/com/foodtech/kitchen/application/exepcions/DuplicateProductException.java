package com.foodtech.kitchen.application.exepcions;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String name) {
        super("Product already exists with name: " + name);
    }
}
