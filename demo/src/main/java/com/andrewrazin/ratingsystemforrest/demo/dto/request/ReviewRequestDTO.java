package com.andrewrazin.ratingsystemforrest.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO(
        @NotNull(message = "Visitor ID is required")
        Long visitorId,

        @NotNull(message = "Restaurant ID is required")
        Long restaurantId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,

        String reviewText
) {}