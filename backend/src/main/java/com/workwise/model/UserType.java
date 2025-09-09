package com.workwise.model;

public enum UserType {
    WORKER("Worker - Provides services"),
    HIRER("Hirer - Seeks services"),
    BOTH("Both - Can provide and seek services");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
