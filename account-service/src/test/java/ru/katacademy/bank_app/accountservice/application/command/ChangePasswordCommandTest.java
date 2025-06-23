package ru.katacademy.bank_app.accountservice.application.command;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест на то, что ChangePasswordCommand корректно создаётся и передаётся в метод changePassword у UserServiceImpl.
 */
public class ChangePasswordCommandTest {
    @Test
    void shouldStoreAndReturnFieldsCorrectly() {
        // given
        final Long userId = 42L;
        final String oldPassword = "oldPass123";
        final String newPassword = "newPass456";

        // when
        final ChangePasswordCommand command = new ChangePasswordCommand(userId, oldPassword, newPassword);

        // then
        assertThat(command.getUserId()).isEqualTo(userId);
        assertThat(command.getOldPassword()).isEqualTo(oldPassword);
        assertThat(command.getNewPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldAllowFieldModificationUsingSetters() {
        // given
        final ChangePasswordCommand command = new ChangePasswordCommand();

        // when
        command.setUserId(99L);
        command.setOldPassword("abc123");
        command.setNewPassword("xyz456");

        // then
        assertThat(command.getUserId()).isEqualTo(99L);
        assertThat(command.getOldPassword()).isEqualTo("abc123");
        assertThat(command.getNewPassword()).isEqualTo("xyz456");
    }
}
