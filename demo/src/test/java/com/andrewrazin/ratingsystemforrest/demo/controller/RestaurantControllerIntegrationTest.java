package com.andrewrazin.ratingsystemforrest.demo.controller;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import com.andrewrazin.ratingsystemforrest.demo.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    void createRestaurant_ShouldReturnCreatedResponse() throws Exception {
        // Arrange
        RestaurantRequestDTO requestDTO = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );

        RestaurantResponseDTO responseDTO = new RestaurantResponseDTO(
                1L,
                "Test Restaurant",
                "Test description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00"),
                new BigDecimal("0.00")
        );

        when(restaurantService.save(any(RestaurantRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.cuisineType").value("ITALIAN"))
                .andExpect(jsonPath("$.averageBill").value(1500.00));

        verify(restaurantService).save(any(RestaurantRequestDTO.class));
    }

    @Test
    void getAllRestaurants_ShouldReturnListOfRestaurants() throws Exception {
        // Arrange
        RestaurantResponseDTO restaurant1 = new RestaurantResponseDTO(
                1L, "Restaurant 1", "Desc 1", CuisineType.ITALIAN,
                new BigDecimal("1000.00"), new BigDecimal("4.50")
        );
        RestaurantResponseDTO restaurant2 = new RestaurantResponseDTO(
                2L, "Restaurant 2", "Desc 2", CuisineType.JAPANESE,
                new BigDecimal("2000.00"), new BigDecimal("4.00")
        );

        when(restaurantService.findAll()).thenReturn(Arrays.asList(restaurant1, restaurant2));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Restaurant 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Restaurant 2"));

        verify(restaurantService).findAll();
    }

    @Test
    void getRestaurantById_WhenRestaurantExists_ShouldReturnRestaurant() throws Exception {
        // Arrange
        RestaurantResponseDTO restaurant = new RestaurantResponseDTO(
                1L, "Test Restaurant", "Test description", CuisineType.ITALIAN,
                new BigDecimal("1500.00"), new BigDecimal("4.50")
        );

        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.cuisineType").value("ITALIAN"));

        verify(restaurantService).findById(1L);
    }

    @Test
    void getRestaurantById_WhenRestaurantNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(restaurantService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/999"))
                .andExpect(status().isNotFound());

        verify(restaurantService).findById(999L);
    }

    @Test
    void updateRestaurant_WhenRestaurantExists_ShouldReturnUpdatedRestaurant() throws Exception {
        // Arrange
        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO(
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00")
        );

        RestaurantResponseDTO updatedRestaurant = new RestaurantResponseDTO(
                1L,
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00"),
                new BigDecimal("4.50")
        );

        when(restaurantService.update(eq(1L), any(RestaurantRequestDTO.class))).thenReturn(updatedRestaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Restaurant"))
                .andExpect(jsonPath("$.cuisineType").value("JAPANESE"));

        verify(restaurantService).update(eq(1L), any(RestaurantRequestDTO.class));
    }

    @Test
    void updateRestaurant_WhenRestaurantNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO(
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00")
        );

        when(restaurantService.update(eq(999L), any(RestaurantRequestDTO.class)))
                .thenThrow(new RuntimeException("Restaurant not found"));

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(restaurantService).update(eq(999L), any(RestaurantRequestDTO.class));
    }

    @Test
    void deleteRestaurant_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(restaurantService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());

        verify(restaurantService).delete(1L);
    }

    @Test
    void getRestaurantsWithMinRating_ShouldReturnFilteredRestaurants() throws Exception {
        // Arrange
        RestaurantResponseDTO restaurant = new RestaurantResponseDTO(
                1L, "Good Restaurant", "Excellent", CuisineType.ITALIAN,
                new BigDecimal("1500.00"), new BigDecimal("4.50")
        );

        when(restaurantService.findRestaurantsWithMinRating(new BigDecimal("4.0")))
                .thenReturn(Arrays.asList(restaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/filter/by-rating")
                        .param("minRating", "4.0")
                        .param("sortByRating", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(4.50));

        verify(restaurantService).findRestaurantsWithMinRating(new BigDecimal("4.0"));
    }
}