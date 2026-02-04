package ru.katacademy.bank_app.audit.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank.events.user.v1.UserRegisteredEvent;
import ru.katacademy.bank_app.audit.application.service.AuditService;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredAuditConsumer {

    private final AuditService auditService;

    @KafkaListener(
            topics = "user.registered",
            groupId = "audit-service-group"
    )
    public void handle(UserRegisteredEvent event) {
        log.info("User registered event received: username={}",
                event.getUsername());

        auditService.saveUserRegisteredEvent(event);
    }
}
