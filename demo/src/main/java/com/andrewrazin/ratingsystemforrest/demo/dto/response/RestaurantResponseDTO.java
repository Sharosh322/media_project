package com.andrewrazin.ratingsystemforrest.demo.dto.response;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import java.math.BigDecimal;

public record RestaurantResponseDTO(
        Long id,
        String name,
        String description,
        CuisineType cuisineType,
        BigDecimal averageBill,
        BigDecimal rating
) {}
