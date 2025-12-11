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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final VisitorRepository visitorRepository;
    private final RestaurantService restaurantService;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         RestaurantRepository restaurantRepository,
                         VisitorRepository visitorRepository,
                         RestaurantService restaurantService,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.visitorRepository = visitorRepository;
        this.restaurantService = restaurantService;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public ReviewResponseDTO save(ReviewRequestDTO reviewRequestDTO) {
        // Проверяем существование ресторана и посетителя
        Restaurant restaurant = restaurantRepository.findById(reviewRequestDTO.restaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + reviewRequestDTO.restaurantId()));

        Visitor visitor = visitorRepository.findById(reviewRequestDTO.visitorId())
                .orElseThrow(() -> new RuntimeException("Visitor not found with id: " + reviewRequestDTO.visitorId()));

        // Проверяем, не оставлял ли уже пользователь отзыв для этого ресторана
        Optional<Review> existingReview = reviewRepository.findByVisitorIdAndRestaurantId(
                reviewRequestDTO.visitorId(), reviewRequestDTO.restaurantId());

        if (existingReview.isPresent()) {
            throw new RuntimeException("Visitor has already reviewed this restaurant");
        }

        // Создаем отзыв
        Review review = new Review(visitor, restaurant,
                reviewRequestDTO.rating(), reviewRequestDTO.reviewText());

        Review savedReview = reviewRepository.save(review);

        // Обновляем рейтинг ресторана
        updateRestaurantRating(restaurant);

        return convertToResponseDTO(savedReview);
    }

    public List<ReviewResponseDTO> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public Optional<ReviewResponseDTO> findById(Long id) {
        return reviewRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    public List<ReviewResponseDTO> findByRestaurantId(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public List<ReviewResponseDTO> findByVisitorId(Long visitorId) {
        List<Review> reviews = reviewRepository.findByVisitorId(visitorId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // Методы для пагинации (Требование 2)
    public Page<ReviewResponseDTO> findByRestaurantIdWithPagination(Long restaurantId,
                                                                    int page,
                                                                    int size,
                                                                    String sortBy,
                                                                    String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findByRestaurantId(restaurantId, pageable);
        return reviewPage.map(this::convertToResponseDTO);
    }

    public Page<ReviewResponseDTO> findByVisitorIdWithPagination(Long visitorId,
                                                                 int page,
                                                                 int size,
                                                                 String sortBy,
                                                                 String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findByVisitorId(visitorId, pageable);
        return reviewPage.map(this::convertToResponseDTO);
    }

    public Page<ReviewResponseDTO> findAllWithPagination(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.map(this::convertToResponseDTO);
    }

    @Transactional
    public ReviewResponseDTO update(Long id, ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        // Проверяем и обновляем связанные сущности при необходимости
        if (!review.getRestaurant().getId().equals(reviewRequestDTO.restaurantId())) {
            Restaurant restaurant = restaurantRepository.findById(reviewRequestDTO.restaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            review.setRestaurant(restaurant);
        }

        if (!review.getVisitor().getId().equals(reviewRequestDTO.visitorId())) {
            Visitor visitor = visitorRepository.findById(reviewRequestDTO.visitorId())
                    .orElseThrow(() -> new RuntimeException("Visitor not found"));
            review.setVisitor(visitor);
        }

        review.setRating(reviewRequestDTO.rating());
        review.setReviewText(reviewRequestDTO.reviewText());

        Review updatedReview = reviewRepository.save(review);

        // Обновляем рейтинг ресторана
        updateRestaurantRating(review.getRestaurant());

        return convertToResponseDTO(updatedReview);
    }

    @Transactional
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        Restaurant restaurant = review.getRestaurant();
        reviewRepository.delete(review);

        // Обновляем рейтинг ресторана после удаления отзыва
        updateRestaurantRating(restaurant);
    }

    private void updateRestaurantRating(Restaurant restaurant) {
        Double averageRating = reviewRepository.calculateAverageRatingByRestaurantId(restaurant.getId());

        if (averageRating != null) {
            BigDecimal rating = BigDecimal.valueOf(averageRating)
                    .setScale(2, RoundingMode.HALF_UP);
            restaurant.setRating(rating);
            restaurantRepository.save(restaurant);
        } else {
            restaurant.setRating(BigDecimal.ZERO);
            restaurantRepository.save(restaurant);
        }
    }

    private ReviewResponseDTO convertToResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getVisitor().getId(),
                review.getVisitor().getName(),
                review.getRestaurant().getId(),
                review.getRestaurant().getName(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt()
        );
    }
}