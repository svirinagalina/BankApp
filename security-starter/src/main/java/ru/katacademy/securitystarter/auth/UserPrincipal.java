package ru.katacademy.securitystarter.auth;

import ru.katacademy.securitystarter.identity.UserIdentity;

/**
 * Представляет аутентифицированного пользователя или сервис в системе.
 *
 * Содержит UserIdentity (USER или SERVICE) для использования в бизнес-логике приложения.
 *
 * @param identity идентичность (user или service)
 *
 * @author Galina
 * @date 2026-01-23
 */
public record UserPrincipal(UserIdentity identity) {

    /**
     * Получить userId для USER-идентичности.
     * Для SERVICE-идентичности вернет null.
     *
     * @return userId или null
     */
    public Long getUserId() {
        return identity.userId();
    }

    /**
     * Получить имя сервиса для SERVICE-идентичности.
     * Для USER-идентичности вернет null.
     *
     * @return serviceName или null
     */
    public String getServiceName() {
        return identity.serviceName();
    }

    /**
     * Проверить, является ли идентичность пользователем.
     *
     * @return true если тип USER
     */
    public boolean isUser() {
        return identity.isUser();
    }

    /**
     * Проверить, является ли идентичность сервисом.
     *
     * @return true если тип SERVICE
     */
    public boolean isService() {
        return identity.isService();
    }
}