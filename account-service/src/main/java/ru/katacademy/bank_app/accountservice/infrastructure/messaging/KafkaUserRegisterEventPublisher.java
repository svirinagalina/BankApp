package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.kafka.KafkaProducer;
import ru.katacademy.bank_shared.event.UserRegisterEvent;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRegisterEventPublisher;

/**
 * Реализация публикатора событий регистрации пользователей через Kafka.
 * <p>
 * Отправляет события о новых регистрациях в указанный Kafka-топик.
 * </p>
 *
 * @author Sheffy
 */
@Component
@RequiredArgsConstructor
public class KafkaUserRegisterEventPublisher implements UserRegisterEventPublisher {

    private final KafkaProducer producer;

    @Override
    public void publish(UserRegisterEvent event) {
        final String message = String.format("Была совершена регистрация пользователя: %s", event);
        producer.send("user-register-event", message);
    }
}
