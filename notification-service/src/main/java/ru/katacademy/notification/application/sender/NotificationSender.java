package ru.katacademy.notification.application.sender;

/**
 * Компонент, отвечающий за отправку уведомлений пользователю.
 */
public interface NotificationSender {

    /**
     * Отправляет сообщение пользователю.
     *
     * @param message текст уведомления
     */
    void send(String message);
}

