package ru.katacademy.bank_app.user.domain.mapper;

import org.springframework.stereotype.Component;

import ru.katacademy.bank_app.user.application.dto.UserDto;
import ru.katacademy.bank_app.user.domain.entity.User;

/**
 * Маппер для преобразования {@link User} в {@link UserDto}.
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-18
 */
@Component
public class UserMapper {
    /**
     * Преобразует сущность User в DTO.
     *
     * @param user пользователь
     * @return DTO пользователя
     */
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail().value(),
                user.getRole()
        );
    }
}