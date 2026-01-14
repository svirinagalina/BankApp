package ru.katacademy.auth.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank.events.password.v1.PasswordChangedEvent;
import ru.katacademy.auth.service.AuthStatisticService;

@Component
@RequiredArgsConstructor
public class PasswordChangedListener {

    private final AuthStatisticService service;

    @KafkaListener(topics = "password-events", groupId = "auth-statistic-service-group")
    public void consume(PasswordChangedEvent event) {
        service.handlePasswordChangedEvent(event);
    }
}
