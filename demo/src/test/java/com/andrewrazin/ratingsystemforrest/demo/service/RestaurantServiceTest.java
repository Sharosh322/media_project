package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import com.andrewrazin.ratingsystemforrest.demo.mapper.RestaurantMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private RestaurantRequestDTO restaurantRequestDTO;
    private RestaurantResponseDTO restaurantResponseDTO;

    @BeforeEach
    void setUp() {
        restaurantRequestDTO = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );

        restaurantResponseDTO = new RestaurantResponseDTO(
                1L,
                "Test Restaurant",
                "Test description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00"),
                new BigDecimal("4.50")
        );

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setDescription("Test description");
        restaurant.setCuisineType(CuisineType.ITALIAN);
        restaurant.setAverageBill(new BigDecimal("1500.00"));
        restaurant.setRating(new BigDecimal("4.50"));
    }

    @Test
    void save_ShouldSetRatingToZeroAndReturnRestaurantResponseDTO() {
        // Arrange
        when(restaurantMapper.toEntity(restaurantRequestDTO)).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant restaurantToSave = invocation.getArgument(0);
            restaurantToSave.setId(1L); // Устанавливаем ID при сохранении
            restaurantToSave.setRating(BigDecimal.ZERO); // Устанавливаем рейтинг в 0
            return restaurantToSave;
        });
        when(restaurantMapper.toResponseDTO(any(Restaurant.class))).thenReturn(restaurantResponseDTO);

        // Act
        RestaurantResponseDTO result = restaurantService.save(restaurantRequestDTO);

        // Assert
        assertNotNull(result);

        // Проверяем, что рейтинг был установлен в 0
        verify(restaurantMapper).toEntity(restaurantRequestDTO);
        verify(restaurantRepository).save(argThat(r ->
                r.getId() == null && // ID еще не установлен
                        r.getRating().equals(BigDecimal.ZERO) // Рейтинг установлен в 0
        ));
        verify(restaurantMapper).toResponseDTO(any(Restaurant.class));
    }

    @Test
    void findAll_ShouldReturnListOfRestaurantResponseDTOs() {
        // Arrange
        List<Restaurant> restaurants = Arrays.asList(restaurant);
        List<RestaurantResponseDTO> expectedResponse = Arrays.asList(restaurantResponseDTO);

        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(restaurantMapper.toResponseDTOList(restaurants)).thenReturn(expectedResponse);

        // Act
        List<RestaurantResponseDTO> result = restaurantService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);

        verify(restaurantRepository).findAll();
        verify(restaurantMapper).toResponseDTOList(restaurants);
    }

    @Test
    void findById_WhenRestaurantExists_ShouldReturnRestaurantResponseDTO() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(restaurantResponseDTO);

        // Act
        Optional<RestaurantResponseDTO> result = restaurantService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(restaurantResponseDTO, result.get()); // ИСПРАВЛЕНО: было visitorResponseDTO

        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper).toResponseDTO(restaurant);
    }

    @Test
    void findById_WhenRestaurantNotExists_ShouldReturnEmptyOptional() {
        // Arrange
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<RestaurantResponseDTO> result = restaurantService.findById(999L);

        // Assert
        assertFalse(result.isPresent());

        verify(restaurantRepository).findById(999L);
        verify(restaurantMapper, never()).toResponseDTO(any());
    }

    @Test
    void update_WhenRestaurantExists_ShouldReturnUpdatedRestaurant() {
        // Arrange
        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO(
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00")
        );

        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(1L);
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setDescription("Updated description");
        updatedRestaurant.setCuisineType(CuisineType.JAPANESE);
        updatedRestaurant.setAverageBill(new BigDecimal("2000.00"));
        updatedRestaurant.setRating(new BigDecimal("4.50"));

        RestaurantResponseDTO updatedResponse = new RestaurantResponseDTO(
                1L,
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00"),
                new BigDecimal("4.50")
        );

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        when(restaurantMapper.toResponseDTO(any(Restaurant.class))).thenReturn(updatedResponse);

        // Act
        RestaurantResponseDTO result = restaurantService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Restaurant", result.name());
        assertEquals(CuisineType.JAPANESE, result.cuisineType());

        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper).updateEntityFromDTO(updateDTO, restaurant);
        verify(restaurantRepository).save(restaurant);
        verify(restaurantMapper).toResponseDTO(updatedRestaurant);
    }

    @Test
    void update_WhenRestaurantNotExists_ShouldThrowException() {
        // Arrange
        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO(
                "Updated Restaurant",
                "Updated description",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00")
        );

        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> restaurantService.update(999L, updateDTO));

        assertEquals("Restaurant not found with id: 999", exception.getMessage());

        verify(restaurantRepository).findById(999L);
        verify(restaurantRepository, never()).save(any());
        verify(restaurantMapper, never()).updateEntityFromDTO(any(), any());
    }

    @Test
    void findRestaurantsWithMinRating_ShouldReturnFilteredRestaurants() {
        // Arrange
        BigDecimal minRating = new BigDecimal("4.0");
        List<Restaurant> filteredRestaurants = Arrays.asList(restaurant);
        List<RestaurantResponseDTO> expectedResponse = Arrays.asList(restaurantResponseDTO);

        when(restaurantRepository.findByRatingGreaterThanEqual(minRating)).thenReturn(filteredRestaurants);
        when(restaurantMapper.toResponseDTOList(filteredRestaurants)).thenReturn(expectedResponse);

        // Act
        List<RestaurantResponseDTO> result = restaurantService.findRestaurantsWithMinRating(minRating);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);

        verify(restaurantRepository).findByRatingGreaterThanEqual(minRating);
        verify(restaurantMapper).toResponseDTOList(filteredRestaurants);
    }

    @Test
    void delete_WhenRestaurantExists_ShouldDeleteRestaurant() {
        // Arrange
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restaurantRepository).deleteById(1L);

        // Act
        restaurantService.delete(1L);

        // Assert
        verify(restaurantRepository).existsById(1L);
        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void delete_WhenRestaurantNotExists_ShouldThrowException() {
        // Arrange
        when(restaurantRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> restaurantService.delete(999L));

        assertEquals("Restaurant not found with id: 999", exception.getMessage());

        verify(restaurantRepository).existsById(999L);
        verify(restaurantRepository, never()).deleteById(any());
    }
}