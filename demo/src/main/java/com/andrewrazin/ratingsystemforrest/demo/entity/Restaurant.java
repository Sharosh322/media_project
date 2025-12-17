package com.andrewrazin.ratingsystemforrest.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(name = "cuisine_type", nullable = false)
    private CuisineType cuisineType;


    @Column(name = "average_bill", nullable = false)
    private BigDecimal averageBill;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    // Конструкторы
    public Restaurant() {}

    public Restaurant(String name, String description, CuisineType cuisineType,
                      BigDecimal averageBill, BigDecimal rating) {
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.averageBill = averageBill;
        this.rating = rating;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public BigDecimal getAverageBill() {
        return averageBill;
    }

    public void setAverageBill(BigDecimal averageBill) {
        this.averageBill = averageBill;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cuisineType=" + cuisineType +
                ", averageBill=" + averageBill +
                ", rating=" + rating +
                '}';
    }
}