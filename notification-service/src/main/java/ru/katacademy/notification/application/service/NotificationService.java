package ru.katacademy.notification.application.service;

import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_shared.event.notification.TransferCompletedEvent;
import ru.katacademy.bank_shared.event.notification.UserRegisteredEvent;

public interface NotificationService {
    void handleUserRegisteredEvent(UserRegisteredEvent event);
    void handleTransferCompletedEvent(TransferCompletedEvent event);
    void handlePasswordChangedEvent(PasswordChangedEvent event);
}
