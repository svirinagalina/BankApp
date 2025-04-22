package ru.katacademy.bank_app.kafka;

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
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение в топик Kafka асинхронно.
     *
     * @param message сообщение, которое будет отправлено в топик "course".
     */
    public void send(String message) {
        kafkaTemplate.send("course", message);
    }
}
