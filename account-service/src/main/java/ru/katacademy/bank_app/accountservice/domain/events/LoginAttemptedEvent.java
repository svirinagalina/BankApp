package ru.katacademy.bank_app.accountservice.domain.events;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Событие попытки входа пользователя в систему.
 * <p>
 * Содержит ключевую информацию для аудита безопасности, анализа аутентификации и анализа сбоев входа.
 * Отправляется в Kafka-топик {@code login-attempts-topic} при каждой попытке входа.
 *
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * LoginAttemptedEvent event = new LoginAttemptedEvent(
 *     123L,
 *     "192.168.1.1",
 *     "Mozilla/5.0",
 *     LocalDateTime.now(),
 *     true
 * );
 * kafkaTemplate.send("login-attempts-topic", event);
 * }</pre>
 * <p>
 * - @param userId      Идентификатор пользователя
 * - @param ip          IP-адрес, с которого выполнена попытка входа
 * - @param userAgent   Данные браузера/устройства пользователя
 * - @param timestamp   Время возникновения события
 * - @param success     Результат попытки (успех/неудача)
 * </p>
 * @author MihasBatler
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginAttemptedEvent {
    private Long userId;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;
    private boolean success;
}
