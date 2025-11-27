package com.andrewrazin.ratingsystemforrest.demo.dto.response;

public record VisitorResponseDTO(
        Long id,
        String name,
        Integer age,
        String gender
) {}
