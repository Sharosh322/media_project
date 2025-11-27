package com.andrewrazin.ratingsystemforrest.demo.controller;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Рестораны", description = "API для управления ресторанами")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @Operation(summary = "Создать новый ресторан")
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(@Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        RestaurantResponseDTO createdRestaurant = restaurantService.save(restaurantRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @GetMapping
    @Operation(summary = "Получить все рестораны")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        List<RestaurantResponseDTO> restaurants = restaurantService.findAll();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить ресторан по ID")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long id) {
        return restaurantService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные ресторана")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        try {
            RestaurantResponseDTO updatedRestaurant = restaurantService.update(id, restaurantRequestDTO);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ресторан")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}