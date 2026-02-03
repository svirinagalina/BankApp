package ru.katacademy.securitystarter.identity;

import java.util.Map;
import java.util.Set;

/**
 * Транспортно-независимая модель идентификации.
 * Описывает субъект запроса — пользователь или сервис.
 */
public record UserIdentity(
        Long userId,
        Set<String> roles,
        Map<String, Object> attributes
) {
    /**
     * Минимальный конструктор — только userId.
     */
    public UserIdentity(Long userId) {
        this(userId, Set.of(), Map.of());
    }
}