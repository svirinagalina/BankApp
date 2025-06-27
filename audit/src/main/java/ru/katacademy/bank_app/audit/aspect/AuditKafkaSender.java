package ru.katacademy.bank_app.audit.aspect;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Сервис для отправки событий аудита в Kafka.
 * Используется аспектом {@link ru.katacademy.bank_app.audit.aspect.AuditAspect}
 * для публикации сообщений о бизнес-операциях.
 */
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "KafkaTemplate управляется Spring и безопасен для прямого использования"
)
@Component
public class AuditKafkaSender {

    /**
     * Компонент Spring Kafka для отправки сообщений.
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param kafkaTemplate шаблон для отправки сообщений в Kafka
     */
    public AuditKafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение в указанный Kafka-топик.
     *
     * @param topic   имя Kafka-топика
     * @param message сериализованное сообщение (например, JSON)
     */
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}


