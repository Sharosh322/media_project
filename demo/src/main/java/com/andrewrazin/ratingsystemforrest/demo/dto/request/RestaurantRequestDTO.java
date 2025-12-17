package com.andrewrazin.ratingsystemforrest.demo.dto.request;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RestaurantRequestDTO(
        @NotNull(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Cuisine type is required")
        CuisineType cuisineType,

        @NotNull(message = "Average bill is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Average bill must be positive")
        BigDecimal averageBill
) {}
