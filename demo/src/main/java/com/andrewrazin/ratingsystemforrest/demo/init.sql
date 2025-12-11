-- Создание базы данных (если запускается без docker-compose)
-- CREATE DATABASE restaurant_db;

-- Подключение к базе данных
-- \c restaurant_db;

-- Расширение для UUID если нужно
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Примечание: Таблицы будут созданы автоматически Hibernate
-- благодаря spring.jpa.hibernate.ddl-auto=update