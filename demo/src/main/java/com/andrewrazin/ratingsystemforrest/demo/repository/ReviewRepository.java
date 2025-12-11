package com.andrewrazin.ratingsystemforrest.demo.repository;

import com.andrewrazin.ratingsystemforrest.demo.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Найти все отзывы для ресторана
    List<Review> findByRestaurantId(Long restaurantId);

    // Найти все отзывы для ресторана с пагинацией
    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    // Найти все отзывы посетителя
    List<Review> findByVisitorId(Long visitorId);

    // Найти все отзывы посетителя с пагинацией
    Page<Review> findByVisitorId(Long visitorId, Pageable pageable);

    // Найти отзыв по посетителю и ресторану (для проверки уникальности)
    Optional<Review> findByVisitorIdAndRestaurantId(Long visitorId, Long restaurantId);

    // JPQL запрос для расчета среднего рейтинга
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double calculateAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);

    // Отзывы с сортировкой по рейтингу (по возрастанию)
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId ORDER BY r.rating ASC")
    List<Review> findReviewsByRestaurantSortedByRatingAsc(@Param("restaurantId") Long restaurantId);

    // Отзывы с сортировкой по рейтингу (по убыванию)
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId ORDER BY r.rating DESC")
    List<Review> findReviewsByRestaurantSortedByRatingDesc(@Param("restaurantId") Long restaurantId);

    // Пагинированные отзывы с сортировкой
    Page<Review> findByRestaurantIdOrderByRatingAsc(Long restaurantId, Pageable pageable);
    Page<Review> findByRestaurantIdOrderByRatingDesc(Long restaurantId, Pageable pageable);
    Page<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId, Pageable pageable);

    // Пагинированные отзывы с сортировкой для посетителя
    Page<Review> findByVisitorIdOrderByRatingAsc(Long visitorId, Pageable pageable);
    Page<Review> findByVisitorIdOrderByRatingDesc(Long visitorId, Pageable pageable);
}