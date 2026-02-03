package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;

@Component
@RequiredArgsConstructor
public class PasswordChangedEventProducer {

    private final KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate;

    public void sendPasswordChangedEvent(Long userId) {
        final PasswordChangedEvent event = new PasswordChangedEvent();
        event.setUserId(userId.toString());
        event.setEventType("PASSWORD_CHANGED");
        event.setOccurredAt(System.currentTimeMillis());
        event.setSource("account-service");

        kafkaTemplate.send("password.changed", event);
    }
}