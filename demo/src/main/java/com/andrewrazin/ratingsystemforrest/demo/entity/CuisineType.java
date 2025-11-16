package com.andrewrazin.ratingsystemforrest.demo.entity;

public enum CuisineType {
    EUROPEAN("Европейская"),
    ITALIAN("Итальянская"),
    CHINESE("Китайская"),
    JAPANESE("Японская"),
    MEXICAN("Мексиканская"),
    INDIAN("Индийская"),
    RUSSIAN("Русская"),
    AMERICAN("Американская");

    private final String displayName;

    CuisineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
