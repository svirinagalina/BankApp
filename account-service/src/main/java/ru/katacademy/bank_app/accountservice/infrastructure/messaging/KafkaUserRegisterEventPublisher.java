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
                        .setEventId(String.valueOf(event.userId()))
                        .setUsername(event.fullName())
                        .setOccurredAt(event.createdAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setSource("account-service")
                        .build();

        producer.send("user.registered", String.valueOf(event.userId()), avroEvent);
    }
}
