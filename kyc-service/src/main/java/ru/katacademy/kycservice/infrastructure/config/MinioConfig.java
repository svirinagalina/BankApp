package ru.katacademy.kycservice.infrastructure.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки клиента MinIO.
 * <p>
 * Поля:
 * - endpoint: URL-адрес сервера Minio
 * - accessKey: ключ доступа к Minio
 * - secretKey: секретный ключ доступа к Minio
 * <p>
 * Методы:
 * - minioClient(): создает и настраивает бин MinioClient для работы с Minio сервером
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

