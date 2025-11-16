package com.andrewrazin.ratingsystemforrest.demo.repository;

import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
