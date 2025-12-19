package ru.katacademy.auth.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Автор: Aleksandr Bronnikov
 * <br>
 * Дата: 16.12.2025
 * <p>Автоматическое создание базы данных при запуске сервиса</p>
 */
@Slf4j
@Configuration
public class DatabaseInitializer {

    private final DataSource dataSource;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schemaName;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        log.info("Запуск инициализации схемы для auth-statistics-service: {}", schemaName);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            log.info("Схема {} успешно проверена/создана", schemaName);
        } catch (Exception e) {
            log.error("Ошибка при создании схемы БД: ", e);
            throw new RuntimeException(e);
        }
    }
}