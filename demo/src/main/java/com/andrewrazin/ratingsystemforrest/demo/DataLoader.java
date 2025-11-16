package com.andrewrazin.ratingsystemforrest.demo;

import com.andrewrazin.ratingsystemforrest.demo.entity.*;
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
        System.out.println("🎯 === НАЧАЛО ТЕСТИРОВАНИЯ СИСТЕМЫ РЕЙТИНГОВ ===");

        // Тестирование сервисов
        testVisitorService();
        testRestaurantService();
        testReviewService();

        // Финальная проверка
        displayFinalResults();

        System.out.println("✅ === ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }

    private void testVisitorService() {
        System.out.println("\n👥 --- ТЕСТИРОВАНИЕ СЕРВИСА ПОСЕТИТЕЛЕЙ ---");

        // Создание посетителей
        Visitor visitor1 = new Visitor("Анна Петрова", 25, "Женский");
        Visitor visitor2 = new Visitor("Иван Сидоров", 30, "Мужской");
        Visitor visitor3 = new Visitor(null, 22, "Женский"); // Анонимный

        visitorService.save(visitor1);
        visitorService.save(visitor2);
        visitorService.save(visitor3);

        System.out.println("✅ Создано посетителей: " + visitorService.findAll().size());

        // Вывод всех посетителей
        List<Visitor> visitors = visitorService.findAll();
        for (Visitor visitor : visitors) {
            System.out.println("   👤 ID: " + visitor.getId() +
                    ", Имя: " + (visitor.getName() != null ? visitor.getName() : "Аноним") +
                    ", Возраст: " + visitor.getAge() +
                    ", Пол: " + visitor.getGender());
        }
    }

    private void testRestaurantService() {
        System.out.println("\n🍕 --- ТЕСТИРОВАНИЕ СЕРВИСА РЕСТОРАНОВ ---");

        // Создание ресторанов
        Restaurant restaurant1 = new Restaurant(
                "Pasta Paradise",
                "Лучшая итальянская кухня в городе",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00"),
                null
        );

        Restaurant restaurant2 = new Restaurant(
                "Суши Мастер",
                "Свежие суши и роллы",
                CuisineType.JAPANESE,
                new BigDecimal("2000.00"),
                null
        );

        Restaurant restaurant3 = new Restaurant(
                "Бургер Хаус",
                "Американская кухня",
                CuisineType.AMERICAN,
                new BigDecimal("800.00"),
                null
        );

        restaurantService.save(restaurant1);
        restaurantService.save(restaurant2);
        restaurantService.save(restaurant3);

        System.out.println("✅ Создано ресторанов: " + restaurantService.findAll().size());

        // Вывод всех ресторанов
        List<Restaurant> restaurants = restaurantService.findAll();
        for (Restaurant restaurant : restaurants) {
            System.out.println("   🏪 ID: " + restaurant.getId() +
                    ", Название: " + restaurant.getName() +
                    ", Кухня: " + restaurant.getCuisineType().getDisplayName() +
                    ", Чек: " + restaurant.getAverageBill() + " руб." +
                    ", Рейтинг: " + (restaurant.getRating() != null ? restaurant.getRating() : "нет оценок"));
        }
    }

    private void testReviewService() {
        System.out.println("\n⭐ --- ТЕСТИРОВАНИЕ СЕРВИСА ОТЗЫВОВ ---");

        // Создание отзывов
        Review review1 = new Review(1L, 1L, 5, "Отличная паста! Обслуживание на высоте.");
        Review review2 = new Review(2L, 1L, 4, "Вкусно, но порции могли бы быть больше.");
        Review review3 = new Review(1L, 2L, 3, "Суши свежие, но маленькие порции.");
        Review review4 = new Review(3L, 3L, 5, "Лучшие бургеры в городе!");
        Review review5 = new Review(2L, 3L, 2, "Пережаренные бургеры, не понравилось.");

        reviewService.save(review1);
        reviewService.save(review2);
        reviewService.save(review3);
        reviewService.save(review4);
        reviewService.save(review5);

        System.out.println("✅ Создано отзывов: " + reviewService.findAll().size());

        // Проверка автоматического пересчета рейтинга
        checkRestaurantRatings();

        // Вывод отзывов по ресторанам
        displayReviewsByRestaurant();
    }

    private void checkRestaurantRatings() {
        System.out.println("\n📊 --- ПРОВЕРКА АВТОМАТИЧЕСКОГО РАСЧЕТА РЕЙТИНГОВ ---");

        List<Restaurant> restaurants = restaurantService.findAll();
        for (Restaurant restaurant : restaurants) {
            List<Review> reviews = reviewService.findByRestaurantId(restaurant.getId());
            System.out.println("   🏪 Ресторан '" + restaurant.getName() +
                    "' - рейтинг: " + restaurant.getRating() +
                    " (на основе " + reviews.size() + " отзывов)");
        }
    }

    private void displayReviewsByRestaurant() {
        System.out.println("\n💬 --- ОТЗЫВЫ ПО РЕСТОРАНАМ ---");

        List<Restaurant> restaurants = restaurantService.findAll();
        for (Restaurant restaurant : restaurants) {
            List<Review> reviews = reviewService.findByRestaurantId(restaurant.getId());
            System.out.println("\n   🏪 " + restaurant.getName() + " (" + reviews.size() + " отзывов):");

            for (Review review : reviews) {
                String visitorName = visitorService.findById(review.getVisitorId())
                        .map(v -> v.getName() != null ? v.getName() : "Аноним")
                        .orElse("Неизвестный");

                System.out.println("      ⭐ " + visitorName + ": " + review.getRating() + "/5 - " +
                        (review.getReviewText() != null ? review.getReviewText() : "без комментария"));
            }
        }
    }

    private void displayFinalResults() {
        System.out.println("\n🎉 === ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ ===");
        System.out.println("📈 Всего посетителей: " + visitorService.findAll().size());
        System.out.println("🏪 Всего ресторанов: " + restaurantService.findAll().size());
        System.out.println("⭐ Всего отзывов: " + reviewService.findAll().size());

        // Лучший ресторан по рейтингу
        Restaurant bestRestaurant = restaurantService.findAll().stream()
                .filter(r -> r.getRating() != null)
                .max((r1, r2) -> r1.getRating().compareTo(r2.getRating()))
                .orElse(null);

        if (bestRestaurant != null) {
            System.out.println("🏆 Лучший ресторан: '" + bestRestaurant.getName() +
                    "' с рейтингом " + bestRestaurant.getRating());
        }
    }
}
