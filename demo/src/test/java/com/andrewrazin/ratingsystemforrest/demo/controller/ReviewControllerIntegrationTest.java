package com.andrewrazin.ratingsystemforrest.demo.controller;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    void createReview_ShouldReturnCreatedResponse() throws Exception {
        // Arrange
        ReviewRequestDTO requestDTO = new ReviewRequestDTO(1L, 1L, 5, "Great!");
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(
                1L, 1L, "John Doe", 1L, "Test Restaurant",
                5, "Great!", LocalDateTime.now()
        );

        when(reviewService.save(any(ReviewRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewText").value("Great!"));

        verify(reviewService).save(any(ReviewRequestDTO.class));
    }

    @Test
    void createReview_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(null, null, 6, "");

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).save(any(ReviewRequestDTO.class));
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                2L, 2L, "Jane", 1L, "Restaurant 1", 4, "Good", LocalDateTime.now()
        );

        when(reviewService.findAll()).thenReturn(Arrays.asList(review1, review2));

        // Act & Assert
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].rating").value(4));

        verify(reviewService).findAll();
    }

    @Test
    void getReviewById_WhenReviewExists_ShouldReturnReview() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );

        when(reviewService.findById(1L)).thenReturn(Optional.of(review));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewText").value("Great!"));

        verify(reviewService).findById(1L);
    }

    @Test
    void getReviewById_WhenReviewNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(reviewService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reviews/999"))
                .andExpect(status().isNotFound());

        verify(reviewService).findById(999L);
    }

    @Test
    void getReviewsByRestaurant_ShouldReturnReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );

        when(reviewService.findByRestaurantId(1L)).thenReturn(Arrays.asList(review));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].restaurantId").value(1L))
                .andExpect(jsonPath("$[0].rating").value(5));

        verify(reviewService).findByRestaurantId(1L);
    }

    @Test
    void getReviewsByVisitor_ShouldReturnReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );

        when(reviewService.findByVisitorId(1L)).thenReturn(Arrays.asList(review));

        // Act & Assert
        mockMvc.perform(get("/api/reviews/visitor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].visitorId").value(1L))
                .andExpect(jsonPath("$[0].rating").value(5));

        verify(reviewService).findByVisitorId(1L);
    }

    @Test
    void updateReview_WhenReviewExists_ShouldReturnUpdatedReview() throws Exception {
        // Arrange
        ReviewRequestDTO updateDTO = new ReviewRequestDTO(1L, 1L, 4, "Updated review");
        ReviewResponseDTO updatedReview = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 4, "Updated review", LocalDateTime.now()
        );

        when(reviewService.update(eq(1L), any(ReviewRequestDTO.class))).thenReturn(updatedReview);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.reviewText").value("Updated review"));

        verify(reviewService).update(eq(1L), any(ReviewRequestDTO.class));
    }

    @Test
    void updateReview_WhenReviewNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        ReviewRequestDTO updateDTO = new ReviewRequestDTO(1L, 1L, 4, "Updated review");

        when(reviewService.update(eq(999L), any(ReviewRequestDTO.class)))
                .thenThrow(new RuntimeException("Review not found"));

        // Act & Assert
        mockMvc.perform(put("/api/reviews/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(reviewService).update(eq(999L), any(ReviewRequestDTO.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(reviewService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(1L);
    }

    @Test
    void getAllReviewsPaged_ShouldReturnPaginatedReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );
        Page<ReviewResponseDTO> page = new PageImpl<>(Arrays.asList(review),
                PageRequest.of(0, 10), 1);

        when(reviewService.findAllWithPagination(eq(0), eq(10), eq("rating"), eq("desc")))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].rating").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(reviewService).findAllWithPagination(0, 10, "rating", "desc");
    }

    @Test
    void getReviewsByRestaurantPaged_ShouldReturnPaginatedReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );
        Page<ReviewResponseDTO> page = new PageImpl<>(Arrays.asList(review),
                PageRequest.of(0, 10), 1);

        when(reviewService.findByRestaurantIdWithPagination(eq(1L), eq(0), eq(10), eq("rating"), eq("desc")))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/restaurant/1/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].restaurantId").value(1L));

        verify(reviewService).findByRestaurantIdWithPagination(1L, 0, 10, "rating", "desc");
    }

    @Test
    void getReviewsByVisitorPaged_ShouldReturnPaginatedReviews() throws Exception {
        // Arrange
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 1L, "John", 1L, "Restaurant 1", 5, "Great!", LocalDateTime.now()
        );
        Page<ReviewResponseDTO> page = new PageImpl<>(Arrays.asList(review),
                PageRequest.of(0, 10), 1);

        when(reviewService.findByVisitorIdWithPagination(eq(1L), eq(0), eq(10), eq("rating"), eq("desc")))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/visitor/1/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].visitorId").value(1L));

        verify(reviewService).findByVisitorIdWithPagination(1L, 0, 10, "rating", "desc");
    }
}
