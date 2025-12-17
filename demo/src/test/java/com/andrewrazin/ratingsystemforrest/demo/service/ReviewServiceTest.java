package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import com.andrewrazin.ratingsystemforrest.demo.entity.Review;
import com.andrewrazin.ratingsystemforrest.demo.entity.Visitor;
import com.andrewrazin.ratingsystemforrest.demo.mapper.ReviewMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.RestaurantRepository;
import com.andrewrazin.ratingsystemforrest.demo.repository.ReviewRepository;
import com.andrewrazin.ratingsystemforrest.demo.repository.VisitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private Restaurant restaurant;
    private Visitor visitor;
    private Review review;
    private ReviewRequestDTO reviewRequestDTO;
    private ReviewResponseDTO reviewResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup restaurant
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setRating(new BigDecimal("4.50"));

        // Setup visitor
        visitor = new Visitor();
        visitor.setId(1L);
        visitor.setName("John Doe");

        // Setup review
        review = new Review(visitor, restaurant, 5, "Great food!");
        review.setId(1L);
        review.setCreatedAt(LocalDateTime.now());

        // Setup DTOs
        reviewRequestDTO = new ReviewRequestDTO(1L, 1L, 5, "Great food!");
        reviewResponseDTO = new ReviewResponseDTO(
                1L, 1L, "John Doe", 1L, "Test Restaurant",
                5, "Great food!", LocalDateTime.now()
        );
    }

    @Test
    void save_WhenValidReview_ShouldSaveAndUpdateRestaurantRating() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRatingByRestaurantId(1L)).thenReturn(5.0);

        // Act
        ReviewResponseDTO result = reviewService.save(reviewRequestDTO);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).calculateAverageRatingByRestaurantId(1L);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void save_WhenRestaurantNotFound_ShouldThrowException() {
        // Arrange
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.save(new ReviewRequestDTO(1L, 999L, 5, "Great")));

        assertEquals("Restaurant not found with id: 999", exception.getMessage());
    }

    @Test
    void save_WhenVisitorAlreadyReviewed_ShouldThrowException() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(Optional.of(review));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.save(reviewRequestDTO));

        assertEquals("Visitor has already reviewed this restaurant", exception.getMessage());
    }

    @Test
    void findByRestaurantId_ShouldReturnReviewsForRestaurant() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByRestaurantId(1L)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.findByRestaurantId(1L);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).findByRestaurantId(1L);
    }

    @Test
    void findByVisitorId_ShouldReturnReviewsForVisitor() {
        // Arrange
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByVisitorId(1L)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.findByVisitorId(1L);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).findByVisitorId(1L);
    }

    @Test
    void update_WhenReviewExists_ShouldUpdateAndReturnReview() {
        // Arrange
        ReviewRequestDTO updateDTO = new ReviewRequestDTO(1L, 1L, 4, "Updated review");
        Review updatedReview = new Review(visitor, restaurant, 4, "Updated review");
        updatedReview.setId(1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
        when(reviewRepository.calculateAverageRatingByRestaurantId(1L)).thenReturn(4.5);

        // Act
        ReviewResponseDTO result = reviewService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).calculateAverageRatingByRestaurantId(1L);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void delete_WhenReviewExists_ShouldDeleteAndUpdateRestaurantRating() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.calculateAverageRatingByRestaurantId(1L)).thenReturn(0.0);

        // Act
        reviewService.delete(1L);

        // Assert
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).delete(review);
        verify(reviewRepository).calculateAverageRatingByRestaurantId(1L);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        // Act
        List<ReviewResponseDTO> result = reviewService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_WhenReviewExists_ShouldReturnReview() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // Act
        Optional<ReviewResponseDTO> result = reviewService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        verify(reviewRepository).findById(1L);
    }
}