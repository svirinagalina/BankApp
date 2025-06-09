package ru.katacademy.notification.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_shared.event.notification.TransferCompletedEvent;
import ru.katacademy.bank_shared.event.notification.UserRegisteredEvent;
import ru.katacademy.notification.application.service.NotificationService;

@Component
public class NotificationEventListener {

    private NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        notificationService.handleUserRegisteredEvent(event);
    }

    @KafkaListener(topics = "transfer.completed", groupId = "notification-service")
    public void handleTransferCompleted(TransferCompletedEvent event) {
        notificationService.handleTransferCompletedEvent(event);
    }

    @KafkaListener(topics = "password.changed", groupId = "notification-service")
    public void handlePasswordChanged(PasswordChangedEvent event) {
        notificationService.handlePasswordChangedEvent(event);

    }
}
