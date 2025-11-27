package com.andrewrazin.ratingsystemforrest.demo.service;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.Review;
import com.andrewrazin.ratingsystemforrest.demo.mapper.ReviewMapper;
import com.andrewrazin.ratingsystemforrest.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RestaurantService restaurantService;
    private final VisitorService visitorService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         ReviewMapper reviewMapper,
                         RestaurantService restaurantService,
                         VisitorService visitorService) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.restaurantService = restaurantService;
        this.visitorService = visitorService;
    }

    @Transactional
    public ReviewResponseDTO save(ReviewRequestDTO reviewRequestDTO) {
        // Проверяем существование посетителя и ресторана
        if (!visitorService.existsById(reviewRequestDTO.visitorId())) {
            throw new RuntimeException("Visitor not found with id: " + reviewRequestDTO.visitorId());
        }

        if (!restaurantService.existsById(reviewRequestDTO.restaurantId())) {
            throw new RuntimeException("Restaurant not found with id: " + reviewRequestDTO.restaurantId());
        }

        // Сохраняем отзыв
        Review review = reviewMapper.toEntity(reviewRequestDTO);
        Review savedReview = reviewRepository.save(review);

        // Пересчитываем среднюю оценку ресторана
        recalculateRestaurantRating(reviewRequestDTO.restaurantId());

        return reviewMapper.toResponseDTO(savedReview);
    }

    public List<ReviewResponseDTO> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        return reviewMapper.toResponseDTOList(reviews);
    }

    public Optional<ReviewResponseDTO> findById(Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toResponseDTO);
    }

    public List<ReviewResponseDTO> findByRestaurantId(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        return reviewMapper.toResponseDTOList(reviews);
    }

    public List<ReviewResponseDTO> findByVisitorId(Long visitorId) {
        List<Review> reviews = reviewRepository.findByVisitorId(visitorId);
        return reviewMapper.toResponseDTOList(reviews);
    }

    @Transactional
    public ReviewResponseDTO update(Long id, ReviewRequestDTO reviewRequestDTO) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        Long oldRestaurantId = existingReview.getRestaurantId();

        reviewMapper.updateEntityFromDTO(reviewRequestDTO, existingReview);
        Review updatedReview = reviewRepository.save(existingReview);

        // Пересчитываем рейтинги для старого и нового ресторана
        recalculateRestaurantRating(oldRestaurantId);
        if (!oldRestaurantId.equals(reviewRequestDTO.restaurantId())) {
            recalculateRestaurantRating(reviewRequestDTO.restaurantId());
        }

        return reviewMapper.toResponseDTO(updatedReview);
    }

    @Transactional
    public void delete(Long id) {
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isPresent()) {
            Long restaurantId = reviewOpt.get().getRestaurantId();
            reviewRepository.deleteById(id);
            // Пересчитываем среднюю оценку после удаления
            recalculateRestaurantRating(restaurantId);
        }
    }

    /**
     * Метод для пересчета средней оценки ресторана
     */
    private void recalculateRestaurantRating(Long restaurantId) {
        Double averageRating = reviewRepository.calculateAverageRatingByRestaurantId(restaurantId);
        restaurantService.updateRating(restaurantId, averageRating);
    }
}
