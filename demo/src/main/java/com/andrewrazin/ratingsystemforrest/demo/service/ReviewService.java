package com.andrewrazin.ratingsystemforrest.demo.service;

import com.andrewrazin.ratingsystemforrest.demo.entity.Review;
import com.andrewrazin.ratingsystemforrest.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantService restaurantService;
    private final VisitorService visitorService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         RestaurantService restaurantService,
                         VisitorService visitorService) {
        this.reviewRepository = reviewRepository;
        this.restaurantService = restaurantService;
        this.visitorService = visitorService;
    }

    @Transactional
    public Review save(Review review) {
        // Проверяем существование посетителя и ресторана
        if (!visitorService.existsById(review.getVisitorId())) {
            throw new RuntimeException("Visitor not found with id: " + review.getVisitorId());
        }

        if (!restaurantService.existsById(review.getRestaurantId())) {
            throw new RuntimeException("Restaurant not found with id: " + review.getRestaurantId());
        }

        // Сохраняем отзыв
        Review savedReview = reviewRepository.save(review);

        // Пересчитываем среднюю оценку ресторана
        recalculateRestaurantRating(review.getRestaurantId());

        return savedReview;
    }

    @Transactional
    public void remove(Long id) {
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isPresent()) {
            Long restaurantId = reviewOpt.get().getRestaurantId();
            reviewRepository.deleteById(id);
            // Пересчитываем среднюю оценку после удаления
            recalculateRestaurantRating(restaurantId);
        }
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByRestaurantId(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }

    public List<Review> findByVisitorId(Long visitorId) {
        return reviewRepository.findByVisitorId(visitorId);
    }

    public boolean existsByVisitorIdAndRestaurantId(Long visitorId, Long restaurantId) {
        return reviewRepository.existsByVisitorIdAndRestaurantId(visitorId, restaurantId);
    }

    private void recalculateRestaurantRating(Long restaurantId) {
        Double averageRating = reviewRepository.calculateAverageRatingByRestaurantId(restaurantId);
        restaurantService.updateRating(restaurantId, averageRating);
    }
}
