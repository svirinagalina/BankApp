package ru.katacademy.bank_app.audit.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_app.audit.application.service.AuditService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PasswordChangedAuditConsumer {

    private final AuditService auditService;

    @KafkaListener(
            topics = "password-events",
            groupId = "audit-service-group",
            containerFactory = "kafkaListenerContainerFactory"

    )
    public void handle(PasswordChangedEvent event) {
        log.info("Password changed event received: userId={}, occurredAt={}, source={}",
                event.getUserId(),
                event.getOccurredAt(),
                event.getSource());

        auditService.savePasswordChangeEvent(event);
    }
}
