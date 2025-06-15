package ru.katacademy.bank_shared.event.notification;

/**
 * Событие, представляющее факт изменения пароля пользователем.
 * Используется для отправки уведомления через Kafka.
 */
public class PasswordChangedEvent {
    private String username;

    public PasswordChangedEvent() {
    }

    /**
     * Возвращает имя пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает имя пользователя.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
