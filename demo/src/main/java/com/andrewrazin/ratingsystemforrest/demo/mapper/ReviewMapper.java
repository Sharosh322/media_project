package com.andrewrazin.ratingsystemforrest.demo.mapper;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    Review toEntity(ReviewRequestDTO reviewRequestDTO);

    ReviewResponseDTO toResponseDTO(Review review);

    List<ReviewResponseDTO> toResponseDTOList(List<Review> reviews);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ReviewRequestDTO reviewRequestDTO, @MappingTarget Review review);
}