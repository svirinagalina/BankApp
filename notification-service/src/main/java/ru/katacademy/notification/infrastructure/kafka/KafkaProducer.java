package ru.katacademy.notification.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> delegateTemplate) {
        // Создаём новый экземпляр на основе того же ProducerFactory,
        // чтобы не ссылаться на внешний mutable-объект:
        this.kafkaTemplate = new KafkaTemplate<>(delegateTemplate.getProducerFactory());
    }

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
