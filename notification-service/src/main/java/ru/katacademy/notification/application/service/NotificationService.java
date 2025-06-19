package ru.katacademy.notification.application.service;

import ru.katacademy.bank_shared.event.notification.PasswordChangedEvent;
import ru.katacademy.bank_shared.event.notification.TransferCompletedEvent;
import ru.katacademy.bank_shared.event.notification.UserRegisteredEvent;

/**
 * Сервис для обработки событий и генерации уведомлений пользователям.
 */
public interface NotificationService {

    /**
     * Обрабатывает событие регистрации пользователя и отправляет приветственное уведомление.
     *
     * @param event событие, содержащее информацию о зарегистрированном пользователе
     */
    void handleUserRegisteredEvent(UserRegisteredEvent event);

    /**
     * Обрабатывает событие успешного перевода и отправляет уведомление отправителю.
     *
     * @param event событие, содержащее информацию о переводе (отправитель, получатель, сумма)
     */
    void handleTransferCompletedEvent(TransferCompletedEvent event);

    /**
     * Обрабатывает событие смены пароля и отправляет соответствующее уведомление.
     *
     * @param event событие, содержащее имя пользователя, сменившего пароль
     */
    void handlePasswordChangedEvent(PasswordChangedEvent event);
}

