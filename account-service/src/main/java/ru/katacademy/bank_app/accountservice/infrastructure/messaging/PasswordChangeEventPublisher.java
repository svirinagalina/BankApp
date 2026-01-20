package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.dto.PasswordUpdatedEventV2;

/**
 * Класс {@code PasswordChangeEventPublisher} отвечает за публикацию событий смены пароля пользователей.
 * <p>
 * Использует {@link } для отправки сообщений в Kafka brokers.
 * Когда происходит событие смены пароля, класс публикует соответствующее сообщение в заданный топик.
 *
 * <p>
 *  Пример использования:
 *  <pre>
 *      PasswordChangeEventPublisher publisher = new PasswordChangeEventPublisher(producer);
 *      publisher.publish(new PasswordChangedEvent(...));
 *  </pre>
 * </p>
 *
 * Автор: Колпаков А.С..
 * Дата: 2025-05-07
 */
@Component
@RequiredArgsConstructor
public class PasswordChangeEventPublisher {

    private final PasswordChangedEventProducer passwordChangedEventProducer;

    public void publish(PasswordUpdatedEventV2 event) {
        passwordChangedEventProducer.sendPasswordChangedEvent(Long.valueOf(String.valueOf(event.getUserId())));
    }
}
