package ru.katacademy.bank_app.accountservice.domain.factory;

import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.katacademy.bank_app.accountservice.adapters.web.request.user.RegisterUserRequest;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

/**
 * Фабрика для создания экземпляров {@link User} из команд и DTO.
 * <p>
 * Инкапсулирует логику валидации и преобразования входных данных в доменные сущности.
 * </p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-18
 */
public class UserFactory {
    private UserFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Создает нового пользователя из команды регистрации.
     *
     * @param cmd команда с данными для регистрации
     * @return новый пользователь
     */
    public static User create(RegisterUserRequest cmd) {

        final String passwordHash = BCrypt.hashpw(cmd.password(), BCrypt.gensalt());

        return new User(
                UserRole.USER, // Роль по умолчанию
                cmd.fullName(),
                new Email(cmd.email()),
                passwordHash,
                LocalDateTime.now());
    }
}