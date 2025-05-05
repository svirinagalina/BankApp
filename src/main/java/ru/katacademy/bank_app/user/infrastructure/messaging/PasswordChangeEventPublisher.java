package ru.katacademy.bank_app.user.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.kafka.KafkaProducer;
import ru.katacademy.bank_app.user.application.dto.PasswordChangedEvent;

@Component
@RequiredArgsConstructor
public class PasswordChangeEventPublisher {

    private final KafkaProducer producer;

    public void publish(PasswordChangedEvent event) {
        final String message = String.format("Была совершена смена пароля: %s", event);
        producer.send("user-change-password-event", message);
    }


}
