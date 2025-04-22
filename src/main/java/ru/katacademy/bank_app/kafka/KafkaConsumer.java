package ru.katacademy.bank_app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Сервис, который подписывается на топики в Kafka и обрабатывает полученные сообщения.
 */
@Slf4j
@Service
public class KafkaConsumer {
    /**
     * Метод для получения сообщений из Kafka-топика и их логирования.
     * Этот метод автоматически вызывается, когда поступает новое сообщение в указанный топик.
     *
     * @param message Сообщение, полученное из Kafka топика.
     */
    @KafkaListener(topics = "course", groupId = "my_consumer")
    public void listen(String message) {
        log.info("Received message = " + message);
    }
}
