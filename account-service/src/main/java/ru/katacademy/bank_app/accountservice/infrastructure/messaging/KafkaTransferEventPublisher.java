package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.katacademy.bank_shared.kafka.KafkaProducer;

@Component
public class KafkaTransferEventPublisher implements TransferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransferEventPublisher.class);
    private final KafkaProducer producer;

    @Value("${spring.kafka.topic.transferCompleted:transfer-completed-events}")
    private String topic;

    public KafkaTransferEventPublisher(KafkaProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(TransferCompletedEvent event) {
        final String key = event.eventId().toString();
        final String message = String.format("Был совершен перевод: %s", event);
        producer.send(topic, key, message);
        log.info("Transfer event опубликован: id={} topic={} key={}", event.eventId(), topic, key);
    }
}