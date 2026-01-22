package ru.katacademy.bank_shared.event.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Событие, представляющее факт изменения пароля пользователем.
 * Используется для отправки уведомления через Kafka.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String userId;

    private Long occurredAt;

    private String source;

    private String eventType;
}