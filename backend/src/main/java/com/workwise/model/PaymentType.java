package com.workwise.model;

public enum PaymentType {
    HOURLY("Payment per hour"),
    DAILY("Payment per day"),
    FIXED("Fixed payment for entire job"),
    PER_ACRE("Payment per acre (for agricultural work)"),
    PER_PIECE("Payment per piece/unit completed"),
    WEEKLY("Payment per week"),
    MONTHLY("Payment per month");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
