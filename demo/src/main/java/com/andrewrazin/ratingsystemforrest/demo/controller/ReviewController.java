package com.andrewrazin.ratingsystemforrest.demo.controller;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Отзывы", description = "API для управления отзывами и оценками")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Создать новый отзыв")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        try {
            ReviewResponseDTO createdReview = reviewService.save(reviewRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Получить все отзывы")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отзыв по ID")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Получить отзывы по ресторану")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        List<ReviewResponseDTO> reviews = reviewService.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "Получить отзывы по посетителю")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByVisitor(@PathVariable Long visitorId) {
        List<ReviewResponseDTO> reviews = reviewService.findByVisitorId(visitorId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить отзыв")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        try {
            ReviewResponseDTO updatedReview = reviewService.update(id, reviewRequestDTO);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отзыв")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

