package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Контракт для извлечения идентификатора пользователя из HTTP-запроса.
 *
 * Предоставляет стратегию получения userId, не зависящую от конкретного
 * механизма аутентификации (заголовок, JWT-токен и т.д.).
 *
 * @author Galina
 * @date 2026-01-23
 */
public interface UserIdentityResolver {

    /**
     * Извлекает идентификатор пользователя из HTTP-запроса.
     *
     * @param request HTTP-запрос
     * @return идентификатор пользователя или null, если не найден
     */
    Long resolve(HttpServletRequest request);
}