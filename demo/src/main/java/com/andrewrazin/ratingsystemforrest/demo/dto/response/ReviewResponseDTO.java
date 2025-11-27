package com.andrewrazin.ratingsystemforrest.demo.dto.response;

public record ReviewResponseDTO(
        Long id,
        Long visitorId,
        Long restaurantId,
        Integer rating,
        String reviewText
) {}