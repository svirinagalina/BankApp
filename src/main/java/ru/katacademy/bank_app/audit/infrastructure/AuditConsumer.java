package ru.katacademy.bank_app.audit.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;
import ru.katacademy.bank_app.account.domain.repository.AuditRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditRepository auditRepository;

    @KafkaListener(topics = "user-register-event", groupId = "audit")
    public void consumeUserRegister(String message) {
        log.info(message);

        AuditEntry auditEntry = new AuditEntry(
                0L, // Временно, потом нужно парсить из получаемого параметра в методе
                "REGISTRATION",
                message,
                LocalDateTime.now(),
                "SUCCESS"
        );

        auditRepository.save(auditEntry);
    }

    @KafkaListener(topics = "transfer-completed-events", groupId = "audit")
    public void consumeTransferCompleted(String message) {
        log.info(message);

        AuditEntry auditEntry = new AuditEntry(
                0L, // Временно, потом нужно парсить из получаемого параметра в методе
                "TRANSFER",
                message,
                LocalDateTime.now(),
                "SUCCESS"
        );

        auditRepository.save(auditEntry);
    }
}
