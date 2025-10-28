package ru.katacademy.bank_app.frauddetection.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.frauddetection.client.FraudAnalysisMessage;
import ru.katacademy.bank_app.frauddetection.service.FraudDetectionService;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransferEventConsumer {

    private final FraudDetectionService fraudService;

    @KafkaListener(topics = "transfer-completed-events", groupId = "fraud-detection-group")
    public void consume(FraudAnalysisMessage message) {
        fraudService.analyze(message.transferEvent(), message.accountDto());
    }
}
