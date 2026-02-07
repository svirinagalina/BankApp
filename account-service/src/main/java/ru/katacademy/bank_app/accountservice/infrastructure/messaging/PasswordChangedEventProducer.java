package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordChangedEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PasswordChangedEventProducer.class);
    private final AvroKafkaProducer producer;

    public void sendPasswordChangedEvent(Long userId) {
        final ru.katacademy.bank.events.password.v1.PasswordChangedEvent avroEvent =
                ru.katacademy.bank.events.password.v1.PasswordChangedEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setUserId(userId.toString())
                        .setUsername(null) // Username не доступен в текущем контексте
                        .setEventType("PASSWORD_CHANGED")
                        .setOccurredAt(System.currentTimeMillis())
                        .setSource("account-service")
                        .build();

        producer.send("password.changed", userId.toString(), avroEvent);
        log.info("Password changed event published: userId={}", userId);
    }
}