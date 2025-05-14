package ru.katacademy.bank_app.notification.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


/**
 * Компонент, отвечающий за получение уведомлений из Kafka.
 * <p>
 * Слушает события регистрации пользователей из топика {@code user-register-event}
 * и логирует полученные сообщения.
 * </p>
 *
 * @author Sheffy
 */
@Slf4j
@Component
public class NotificationConsumer {
    @KafkaListener(topics = "user-register-event", groupId = "settings-group")
    public void handleUserRegisteredEvent(String message) {
        log.info(message);
    }
}
