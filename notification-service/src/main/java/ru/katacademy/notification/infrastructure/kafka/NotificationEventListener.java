package ru.katacademy.notification.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_shared.event.notification.TransferCompletedEvent;
import ru.katacademy.bank_shared.event.notification.UserRegisteredEvent;
import ru.katacademy.notification.application.service.NotificationService;

/**
 * Kafka-сервис для обработки входящих событий уведомлений.
 * Слушает события из Kafka и делегирует их обработку {@link NotificationService}.
 */
@Component
public class NotificationEventListener {

    private NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Обрабатывает событие регистрации пользователя.
     * Топик: user.registered
     *
     * @param event событие регистрации пользователя
     */
    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        notificationService.handleUserRegisteredEvent(event);
    }

    /**
     * Обрабатывает событие завершения перевода.
     * Топик: transfer.completed
     *
     * @param event событие перевода
     */
    @KafkaListener(topics = "transfer.completed", groupId = "notification-service")
    public void handleTransferCompleted(TransferCompletedEvent event) {
        notificationService.handleTransferCompletedEvent(event);
    }

    /**
     * Обрабатывает событие изменения пароля.
     * Топик: password.changed
     *
     * @param event событие смены пароля
     */
    @KafkaListener(topics = "password.changed", groupId = "notification-service")
    public void handlePasswordChanged(PasswordChangedEvent event) {
        notificationService.handlePasswordChangedEvent(event);
    }
}

