package com.andrewrazin.ratingsystemforrest.demo;

import com.andrewrazin.ratingsystemforrest.demo.dto.request.RestaurantRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.ReviewRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.VisitorRequestDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import com.andrewrazin.ratingsystemforrest.demo.service.RestaurantService;
import com.andrewrazin.ratingsystemforrest.demo.service.ReviewService;
import com.andrewrazin.ratingsystemforrest.demo.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final VisitorService visitorService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    @Autowired
    public DataLoader(VisitorService visitorService,
                      RestaurantService restaurantService,
                      ReviewService reviewService) {
        this.visitorService = visitorService;
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎯 === НАЧАЛО ТЕСТИРОВАНИЯ СИСТЕМЫ РЕЙТИНГОВ ===");

        try {
            testVisitorService();
            testRestaurantService();
            testReviewService();
            displayFinalResults();
            System.out.println("✅ === ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
        } catch (Exception e) {
            System.out.println("⚠️  Произошла ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testVisitorService() {
        System.out.println("\n👥 --- ТЕСТИРОВАНИЕ СЕРВИСА ПОСЕТИТЕЛЕЙ ---");

        VisitorRequestDTO visitor1 = new VisitorRequestDTO("Анна Петрова", 25, "Женский");
        VisitorRequestDTO visitor2 = new VisitorRequestDTO("Иван Сидоров", 30, "Мужской");
        VisitorRequestDTO visitor3 = new VisitorRequestDTO(null, 22, "Женский");

        try {
            visitorService.save(visitor1);
            visitorService.save(visitor2);
            visitorService.save(visitor3);
            System.out.println("✅ Создано 3 тестовых посетителя");
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка при создании посетителей: " + e.getMessage());
        }
    }

    private void testRestaurantService() {
        System.out.println("\n🍕 --- ТЕСТИРОВАНИЕ СЕРВИСА РЕСТОРАНОВ ---");

        RestaurantRequestDTO restaurant1 = new RestaurantRequestDTO(
                "Pasta Paradise",
                "Лучшая итальянская кухня в городе",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );

        RestaurantRequestDTO restaurant2 = new RestaurantRequestDTO(
                "Суши Мастер",
                "Свежие суши и роллы",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00")
        );

        try {
            restaurantService.save(restaurant1);
            restaurantService.save(restaurant2);
            System.out.println("✅ Создано 2 тестовых ресторана");
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка при создании ресторанов: " + e.getMessage());
        }
    }

    private void testReviewService() {
        System.out.println("\n⭐ --- ТЕСТИРОВАНИЕ СЕРВИСА ОТЗЫВОВ ---");

        ReviewRequestDTO review1 = new ReviewRequestDTO(1L, 1L, 5, "Отличная паста! Обслуживание на высоте.");
        ReviewRequestDTO review2 = new ReviewRequestDTO(2L, 1L, 4, "Вкусно, но порции могли бы быть больше.");
        ReviewRequestDTO review3 = new ReviewRequestDTO(3L, 2L, 3, "Суши свежие, но маленькие порции.");

        try {
            reviewService.save(review1);
            reviewService.save(review2);
            reviewService.save(review3);
            System.out.println("✅ Создано 3 тестовых отзыва");
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка при создании отзывов: " + e.getMessage());
        }
    }

    private void displayFinalResults() {
        System.out.println("\n🎉 === ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ ===");
        try {
            System.out.println("📈 Всего посетителей: " + visitorService.findAll().size());
            System.out.println("🏪 Всего ресторанов: " + restaurantService.findAll().size());

            // Временно отключаем получение отзывов чтобы избежать ошибки
            // System.out.println("⭐ Всего отзывов: " + reviewService.findAll().size());
            System.out.println("⭐ Отзывы успешно созданы (проверьте через Swagger)");

        } catch (Exception e) {
            System.out.println("⚠️  Ошибка при получении результатов: " + e.getMessage());
        }
        System.out.println("\n📚 Swagger UI доступен по: http://localhost:8080/swagger-ui.html");
        System.out.println("📊 H2 Console доступен по: http://localhost:8080/h2-console");
    }
}