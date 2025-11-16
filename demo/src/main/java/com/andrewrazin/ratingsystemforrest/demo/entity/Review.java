package com.andrewrazin.ratingsystemforrest.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false)
    private Long visitorId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    // Конструкторы
    public Review() {}

    public Review(Long visitorId, Long restaurantId, Integer rating, String reviewText) {
        this.visitorId = visitorId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Long visitorId) {
        this.visitorId = visitorId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", visitorId=" + visitorId +
                ", restaurantId=" + restaurantId +
                ", rating=" + rating +
                ", reviewText='" + reviewText + '\'' +
                '}';
    }
}
