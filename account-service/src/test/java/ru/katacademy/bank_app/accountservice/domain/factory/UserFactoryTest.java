package ru.katacademy.bank_app.accountservice.domain.factory;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

/**
 * Тест проверяет работу методов UserFactory
 */
public class UserFactoryTest {
    // тест, проверяющий корректно ли создается новый пользователь, если ввести валидные данные
    @Test
    void create_ShouldReturnValidUser_WhenCommandIsCorrect() {
        // given
        final String fullName = "Иван Иванов";
        final String rawEmail = "ivan@gmail.com";
        final String password = "Ivan123";
        final RegisterUserCommand command = new RegisterUserCommand(fullName, rawEmail, password);

        // when
        final User user = UserFactory.create(command);

        // then
        assertThat(user.getFullName()).isEqualTo(fullName);
        assertThat(user.getEmail()).isEqualTo(new Email(rawEmail));
        assertThat(BCrypt.checkpw(password, user.getPasswordHash())).isTrue(); // Хеширование пока отсутствует на этапе создания
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(user.getId()).isNull();
    }
}
