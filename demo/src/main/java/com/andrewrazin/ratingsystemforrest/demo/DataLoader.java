package com.andrewrazin.ratingsystemforrest.demo;
import com.andrewrazin.ratingsystemforrest.demo.dto.request.*;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.RestaurantResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.ReviewResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.dto.response.VisitorResponseDTO;
import com.andrewrazin.ratingsystemforrest.demo.entity.CuisineType;
import com.andrewrazin.ratingsystemforrest.demo.entity.Restaurant;
import com.andrewrazin.ratingsystemforrest.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

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
        System.out.println("🎯 === НАЧАЛО ТЕСТИРОВАНИЯ СИСТЕМЫ РЕЙТИНГОВ (DTO версия) ===");

        testVisitorService();
        testRestaurantService();
        testReviewService();

        displayFinalResults();

        System.out.println("✅ === ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }

    private void testVisitorService() {
        System.out.println("\n👥 --- ТЕСТИРОВАНИЕ СЕРВИСА ПОСЕТИТЕЛЕЙ ---");

        VisitorRequestDTO visitor1 = new VisitorRequestDTO("Анна Петрова", 25, "Женский");
        VisitorRequestDTO visitor2 = new VisitorRequestDTO("Иван Сидоров", 30, "Мужской");
        VisitorRequestDTO visitor3 = new VisitorRequestDTO(null, 22, "Женский");

        visitorService.save(visitor1);
        visitorService.save(visitor2);
        visitorService.save(visitor3);

        List<VisitorResponseDTO> visitors = visitorService.findAll();
        System.out.println("✅ Создано посетителей: " + visitors.size());
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

        restaurantService.save(restaurant1);
        restaurantService.save(restaurant2);

        List<RestaurantResponseDTO> restaurants = restaurantService.findAll();
        System.out.println("✅ Создано ресторанов: " + restaurants.size());
    }

    private void testReviewService() {
        System.out.println("\n⭐ --- ТЕСТИРОВАНИЕ СЕРВИСА ОТЗЫВОВ ---");

        ReviewRequestDTO review1 = new ReviewRequestDTO(1L, 1L, 5, "Отличная паста! Обслуживание на высоте.");
        ReviewRequestDTO review2 = new ReviewRequestDTO(2L, 1L, 4, "Вкусно, но порции могли бы быть больше.");
        ReviewRequestDTO review3 = new ReviewRequestDTO(1L, 2L, 3, "Суши свежие, но маленькие порции.");

        reviewService.save(review1);
        reviewService.save(review2);
        reviewService.save(review3);

        List<ReviewResponseDTO> reviews = reviewService.findAll();
        System.out.println("✅ Создано отзывов: " + reviews.size());
    }

    private void displayFinalResults() {
        System.out.println("\n🎉 === ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ ===");
        System.out.println("📈 Всего посетителей: " + visitorService.findAll().size());
        System.out.println("🏪 Всего ресторанов: " + restaurantService.findAll().size());
        System.out.println("⭐ Всего отзывов: " + reviewService.findAll().size());
        System.out.println("\n📚 Swagger UI доступен по: http://localhost:8080/swagger-ui.html");
        System.out.println("📊 H2 Console доступен по: http://localhost:8080/h2-console");
    }
}