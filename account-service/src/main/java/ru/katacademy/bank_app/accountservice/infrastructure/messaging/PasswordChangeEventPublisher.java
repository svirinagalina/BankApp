package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.dto.PasswordChangedEvent;
import ru.katacademy.bank_shared.kafka.KafkaProducer;

/**
 * Класс {@code PasswordChangeEventPublisher} отвечает за публикацию событий смены пароля пользователей.
 *
 * Использует {@link KafkaProducer} для отправки сообщений в Kafka brokers.
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

    private final KafkaProducer producer;

    public void publish(PasswordChangedEvent event) {
        final String message = String.format("Была совершена смена пароля: %s", event);
        producer.send("user-change-password-event", message);
    }


}
