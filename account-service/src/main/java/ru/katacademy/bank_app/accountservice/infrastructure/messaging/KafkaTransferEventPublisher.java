package ru.katacademy.bank_app.accountservice.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;

@Component
public class KafkaTransferEventPublisher implements TransferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransferEventPublisher.class);
    private final AvroKafkaProducer producer;

    @Value("${spring.kafka.topic.transferCompleted:transfer.completed}")
    private String topic;

    public KafkaTransferEventPublisher(AvroKafkaProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(TransferCompletedEvent event) {
        final ru.katacademy.bank.events.transfer.v1.TransferCompletedEvent avroEvent =
                ru.katacademy.bank.events.transfer.v1.TransferCompletedEvent.newBuilder()
                        .setEventId(event.eventId().toString())
                        .setAccountNumberFrom(event.accountNumberFrom().value())
                        .setAccountNumberTo(event.accountNumberTo().value())
                        .setAmount(event.money().amount().toString())
                        .setCurrency(event.money().currency().toString())
                        .setOccurredAt(event.localDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .setSource("account-service")
                        .build();

        final String key = event.eventId().toString();
        producer.send(topic, key, avroEvent);
        log.info("Transfer event опубликован: id={} topic={} key={}", event.eventId(), topic, key);
    }
}