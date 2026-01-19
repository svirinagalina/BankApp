package ru.katacademy.apigateway.dto;

/**
 * DTO для представления ссылки на Swagger UI сервиса.
 *
 * Используется {@link SwaggerEndpointResolver} для генерации списка ссылок.
 *
 * @param name имя сервиса
 * @param url  полный URL к Swagger UI сервиса
 * author: Krasitskii Dmitrii
 * date: 17.01.2026
 */
public record SwaggerLink(
        String name,
        String url
) {}