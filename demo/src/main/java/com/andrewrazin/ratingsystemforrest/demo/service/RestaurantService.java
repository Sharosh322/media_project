package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import com.andrewrazin.ratingsystemforrest.demo.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public void remove(Long id) {
        restaurantRepository.deleteById(id);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
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
}
