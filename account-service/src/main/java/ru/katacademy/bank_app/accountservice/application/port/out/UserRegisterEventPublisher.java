package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_shared.event.UserRegisterEvent;

/**
 * Интерфейс для публикации события регистрации пользователя.
 *
 * @author Sheffy
 */
public interface UserRegisterEventPublisher {
    void publish(UserRegisterEvent event);
}
