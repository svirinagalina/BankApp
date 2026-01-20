package ru.katacademy.bank_shared.event.notification;

import lombok.Getter;
import lombok.Setter;

/**
 * Событие, представляющее факт изменения пароля пользователем.
 * Используется для отправки уведомления через Kafka.
 */
@Setter
@Getter
public class PasswordChangedEvent {
    /**
     * -- GETTER --
     *  Возвращает имя пользователя.
     * -- SETTER --
     *  Устанавливает имя пользователя.

     */
    private String username;
    /**
     * -- GETTER --
     *  Возвращает ID пользователя.
     * -- SETTER --
     *  Устанавливает ID пользователя.

     */
    private String userId;
    /**
     * -- GETTER --
     *  Возвращает timestamp события в миллисекундах.
     * -- SETTER --
     *  Устанавливает timestamp события в миллисекундах.

     */
    private Long occurredAt;
    /**
     * -- GETTER --
     *  Возвращает источник события.
     * -- SETTER --
     *  Устанавливает источник события.

     */
    private String source;
    /**
     * -- GETTER --
     *  Возвращает тип события.
     * -- SETTER --
     *  Устанавливает тип события.

     */
    private String eventType;

    public PasswordChangedEvent() {
        throw new UnsupportedOperationException("Use builder or parameterized constructor");
    }

}