package ru.katacademy.bank_app.audit.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank.events.transfer.v1.TransferCompletedEvent;
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
                event.getEventId(),
                event.getAccountNumberFrom(),
                event.getAccountNumberTo(),
                event.getAmount());

        auditService.saveTransferCompletedEvent(event);
    }
}
