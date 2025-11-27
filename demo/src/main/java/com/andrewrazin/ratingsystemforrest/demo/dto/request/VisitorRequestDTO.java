package com.andrewrazin.ratingsystemforrest.demo.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VisitorRequestDTO(
        String name, // необязательное поле

        @NotNull(message = "Age is required")
        @Min(value = 0, message = "Age must be positive")
        Integer age,

        @NotNull(message = "Gender is required")
        String gender
) {}
