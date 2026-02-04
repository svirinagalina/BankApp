package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Контракт для извлечения идентичности (user или service) из HTTP-запроса.
 *
 * Предоставляет стратегию получения UserIdentity, не зависящую от конкретного
 * механизма аутентификации (заголовок X-User-Id, X-Service-Name, JWT-токен и т.д.).
 *
 * @author Galina
 * @date 2026-01-23
 */
public interface UserIdentityResolver {

    /**
     * Извлекает идентичность из HTTP-запроса.
     *
     * @param request HTTP-запрос
     * @return UserIdentity (USER или SERVICE) или null, если не найдена
     */
    UserIdentity resolve(HttpServletRequest request);
}