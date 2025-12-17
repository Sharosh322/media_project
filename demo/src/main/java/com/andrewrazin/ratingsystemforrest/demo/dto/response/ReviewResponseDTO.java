package com.andrewrazin.ratingsystemforrest.demo.dto.response;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        Long visitorId,
        String visitorName,
        Long restaurantId,
        String restaurantName,
        Integer rating,
        String reviewText,
        LocalDateTime createdAt
) {}