package ru.katacademy.bank_shared.event.notification;

/**
 * Событие, представляющее регистрацию нового пользователя.
 * Используется для отправки приветственного уведомления через Kafka.
 */
public class UserRegisteredEvent {
    private String username;

    public UserRegisteredEvent() {
    }

    /**
     * Возвращает имя нового пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает имя нового пользователя.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}

