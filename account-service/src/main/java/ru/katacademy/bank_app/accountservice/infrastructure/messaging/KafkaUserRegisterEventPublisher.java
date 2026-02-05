package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.port.out.UserRegisterEventPublisher;
import ru.katacademy.bank_shared.event.UserRegisterEvent;

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

    private final AvroKafkaProducer producer;

    @Override
    public void publish(UserRegisterEvent event) {
        final ru.katacademy.bank.events.user.v1.UserRegisteredEvent avroEvent =
                ru.katacademy.bank.events.user.v1.UserRegisteredEvent.newBuilder()
                        .setEventId(event.eventId().toString())
                        .setUsername(event.username())
                        .setOccurredAt(event.occurredAt().toEpochMilli())
                        .setSource(event.source())
                        .build();

        producer.send("user.registered", event.eventId().toString(), avroEvent);
    }
}
