package ru.katacademy.securitystarter.auth;

/**
 * Представляет аутентифицированного пользователя в системе.
 *
 * Содержит минимальную информацию о пользователе (userId)
 * для использования в бизнес-логике приложения.
 *
 * @param userId идентификатор пользователя
 *
 * @author Galina
 * @date 2026-01-23
 */
public record UserPrincipal(Long userId) {
}