package com.andrewrazin.ratingsystemforrest.demo.mapper;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    Restaurant toEntity(RestaurantRequestDTO restaurantRequestDTO);

    RestaurantResponseDTO toResponseDTO(Restaurant restaurant);

    List<RestaurantResponseDTO> toResponseDTOList(List<Restaurant> restaurants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    void updateEntityFromDTO(RestaurantRequestDTO restaurantRequestDTO, @MappingTarget Restaurant restaurant);
}