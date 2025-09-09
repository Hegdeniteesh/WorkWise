package com.workwise.model;

public enum SkillCategory {
    // Construction & Building Trades
    CONSTRUCTION("General Construction Work"),
    ELECTRICAL("Electrical Installation & Repair"),
    PLUMBING("Plumbing Services"),
    CARPENTRY("Carpentry & Woodwork"),
    PAINTING("Painting & Decoration"),
    MASONRY("Masonry & Stonework"),

    // Agricultural & Farming
    FARMING("General Farming"),
    CROP_HARVESTING("Crop Harvesting"),
    LIVESTOCK("Livestock Management"),
    IRRIGATION("Irrigation Systems"),
    AGRICULTURAL_MACHINERY("Agricultural Equipment Operation"),
    ORGANIC_FARMING("Organic Farming Practices"),

    // Domestic & Household Services
    CLEANING("Cleaning Services"),
    COOKING("Cooking & Food Preparation"),
    CHILDCARE("Childcare Services"),
    ELDERCARE("Elder Care Services"),
    GARDENING("Gardening & Landscaping"),
    LAUNDRY("Laundry & Ironing"),

    // Retail & Commercial
    RETAIL_ASSISTANCE("Retail & Sales Assistance"),
    INVENTORY_MANAGEMENT("Inventory Management"),
    CASHIER("Cashier Services"),
    DELIVERY("Delivery Services"),
    CUSTOMER_SERVICE("Customer Service"),

    // Creative & Media
    PHOTOGRAPHY("Photography Services"),
    VIDEOGRAPHY("Videography"),
    GRAPHIC_DESIGN("Graphic Design"),
    EVENT_DECORATION("Event Decoration"),
    MUSIC("Music & Entertainment"),

    // Transportation & Logistics
    DRIVING("Driving Services"),
    LOGISTICS("Logistics & Warehousing"),
    MOVING_SERVICES("Moving & Packing Services"),

    // Technology & Digital
    COMPUTER_REPAIR("Computer Repair"),
    MOBILE_REPAIR("Mobile Phone Repair"),
    DATA_ENTRY("Data Entry"),

    // Others
    GENERAL_LABOR("General Labor Work"),
    SECURITY("Security Services"),
    MAINTENANCE("Maintenance Services"),
    TAILORING("Tailoring & Alterations"),
    BEAUTY_SERVICES("Beauty & Wellness Services"),
    TUTORING("Tutoring & Education"),
    OTHER("Other Services");

    private final String description;

    SkillCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
