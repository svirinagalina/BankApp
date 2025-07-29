package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;

import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;

/**
 * Маппер между доменной моделью {@link User} и JPA‑сущностью {@link UserEntity}.
 */
public class UserMapper {

    /**
     * В доменную модель → JPA‑сущность.
     *
     * @param u доменный пользователь
     * @return JPA‑сущность для сохранения
     */
    public static UserEntity toEntity(User u) {
        return new UserEntity(
                u.getId(),
                u.getRole(),
                u.getFullName(),
                u.getEmail(),
                u.getPasswordHash(),
                u.getCreatedAt()
        );
    }

    /**
     * JPA‑сущность → доменную модель.
     *
     * @param e сущность из БД
     * @return доменный пользователь
     */
    public static User toDomain(UserEntity e) {
        return new User(
                e.getId(),
                e.getRole(),
                e.getFullName(),
                e.getEmail(),
                e.getPasswordHash(),
                e.getCreatedAt()
        );
    }
}
