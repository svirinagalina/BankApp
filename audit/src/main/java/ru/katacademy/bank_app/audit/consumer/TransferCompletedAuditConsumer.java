package ru.katacademy.bank_app.audit.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_app.audit.application.service.AuditService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferCompletedAuditConsumer {

    private final AuditService auditService;

    @KafkaListener(
            topics = "transfer.completed",
            groupId = "audit-service-group"
    )
    public void handle(TransferCompletedEvent event) {
        log.info("Transfer completed event received: eventId={}, from={}, to={}, amount={}",
                event.eventId(),
                event.accountNumberFrom().value(),
                event.accountNumberTo().value(),
                event.money().amount());

        auditService.saveTransferCompletedEvent(event);
    }
}
