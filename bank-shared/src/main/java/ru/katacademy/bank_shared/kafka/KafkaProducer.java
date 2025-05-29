package ru.katacademy.bank_shared.kafka;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Класс для отправки сообщений в топики Kafka.
 * Использует {@link KafkaTemplate} для асинхронной отправки сообщений в указанный топик.
 */
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Конструктор для внедрения зависимости KafkaTemplate.
     *
     * @param kafkaTemplate {@link KafkaTemplate} для работы с Kafka.
     */
    @Autowired
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "KafkaTemplate is thread-safe and managed by Spring container"
    )
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение в топик Kafka асинхронно.
     *
     * @param message сообщение, которое будет отправлено в топик "course".
     */
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
