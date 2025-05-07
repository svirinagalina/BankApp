package ru.katacademy.bank_app.frauddetection.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.frauddetection.service.FraudDetectionService;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransferEventConsumer {

    private final FraudDetectionService fraudService;

    @KafkaListener(topics = "transfer-completed-events", groupId = "fraud-detection-group")
    public void consume(TransferCompletedEvent event) {
        fraudService.analyze(event);
    }
}
