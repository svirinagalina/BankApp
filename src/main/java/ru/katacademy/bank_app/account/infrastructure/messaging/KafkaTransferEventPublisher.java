package ru.katacademy.bank_app.account.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.account.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.kafka.KafkaProducer;
import ru.katacademy.bank_app.shared.event.TransferCompletedEvent;

/**
 * Реализация публикатора событий о переводах через Kafka.
 * <p>
 * Отправляет события о завершенных переводах в Kafka-топик.
 * </p>
 *
 * @author Sheffy
 */
@Component
public class KafkaTransferEventPublisher implements TransferEventPublisher {

    private final KafkaProducer producer;

    @Autowired
    public KafkaTransferEventPublisher(final KafkaProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(TransferCompletedEvent event) {
        final String message = String.format("Был совершён перевод: %s", event);
        producer.send("transfer-completed-events", message);
    }
}
