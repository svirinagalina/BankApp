package ru.katacademy.bank_shared.kafka;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.katacademy.bank.events.password.v1.PasswordChangedEvent;

import java.time.Instant;
import java.util.UUID;

@Service
public class KafkaAvroProducer {

    private final KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "KafkaTemplate is thread-safe and managed by Spring container"
    )
    public KafkaAvroProducer(KafkaTemplate<String, PasswordChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPasswordChangedEvent(String userId) {
        PasswordChangedEvent event = PasswordChangedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setUserId(userId)
                .setEventType("PASSWORD_CHANGED")
                .setOccurredAt(Instant.now().toEpochMilli())
                .setSource("user-settings-service")
                .build();

        kafkaTemplate.send("password-events", userId, event);
    }
}
