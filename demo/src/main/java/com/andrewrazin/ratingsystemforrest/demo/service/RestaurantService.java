package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import com.andrewrazin.ratingsystemforrest.demo.mapper.RestaurantMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.RestaurantRepository;
import com.andrewrazin.ratingsystemforrest.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    public RestaurantResponseDTO save(RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(savedRestaurant);
    }

    public List<RestaurantResponseDTO> findAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurantMapper.toResponseDTOList(restaurants);
    }

    public Optional<RestaurantResponseDTO> findById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toResponseDTO);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        restaurantMapper.updateEntityFromDTO(restaurantRequestDTO, existingRestaurant);
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return restaurantMapper.toResponseDTO(updatedRestaurant);
    }

    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return restaurantRepository.existsById(id);
    }

    public void updateRating(Long restaurantId, Double newRating) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        if (newRating != null) {
            restaurant.setRating(java.math.BigDecimal.valueOf(newRating));
            restaurantRepository.save(restaurant);
        }
    }
    public List<RestaurantResponseDTO> findRestaurantsWithMinRating(BigDecimal minRating) {
        // Способ 1: Используя конвенции имен методов
        List<Restaurant> restaurants = restaurantRepository.findByRatingGreaterThanEqual(minRating);
        return restaurants.stream()
                .map(restaurantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RestaurantResponseDTO> findRestaurantsWithMinRatingSorted(BigDecimal minRating) {
        // Способ 2: Используя JPQL запрос с сортировкой
        List<Restaurant> restaurants = restaurantRepository.findRestaurantsWithMinRatingSorted(minRating);
        return restaurants.stream()
                .map(restaurantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RestaurantResponseDTO> findRestaurantsWithMinRatingJPQL(BigDecimal minRating) {
        // Альтернативный JPQL запрос
        List<Restaurant> restaurants = restaurantRepository.findRestaurantsWithMinRating(minRating);
        return restaurants.stream()
                .map(restaurantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
