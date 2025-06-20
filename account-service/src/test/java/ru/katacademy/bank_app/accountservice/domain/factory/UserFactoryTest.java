package ru.katacademy.bank_app.accountservice.domain.factory;

import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.valueobject.Email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

public class UserFactoryTest {
    // тест, проверяющий корректно ли создается новый пользователь, если ввести валидные данные
    @Test
    void create_ShouldReturnValidUser_WhenCommandIsCorrect() {
        // given
        String fullName = "Иван Иванов";
        String rawEmail = "ivan@gmail.com";
        String password = "Ivan123";
        RegisterUserCommand command = new RegisterUserCommand(fullName, rawEmail, password);

        // when
        User user = UserFactory.create(command);

        // then
        assertThat(user.getFullName()).isEqualTo(fullName);
        assertThat(user.getEmail()).isEqualTo(new Email(rawEmail));
        assertThat(user.getPasswordHash()).isEqualTo(password); // Хеширование пока отсутствует на этапе создания
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(user.getId()).isNull();
    }
}
