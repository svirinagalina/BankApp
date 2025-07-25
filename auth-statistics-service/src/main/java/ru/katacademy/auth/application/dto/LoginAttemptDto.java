package ru.katacademy.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для передачи информации о попытке входа в систему.
 * <p>
 * Содержит данные о пользователе, его IP-адресе, данные браузера/устройства пользователя,
 * времени события и результате попытки.
 * Используется для сериализации/десериализации данных при передаче через Kafka или REST API.
 * </p>
 *
 * <p>
 * Аннотации Lombok ({@code @Data}, {@code @AllArgsConstructor} и др.) автоматически генерируют:
 * <ul>
 *   <li>Геттеры и сеттеры для всех полей.</li>
 *   <li>Методы {@code equals()}, {@code hashCode()}.</li>
 *   <li>Конструкторы (с аргументами и по умолчанию).</li>
 * </ul>
 *
 * @author MihasBatler
 */

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttemptDto {
    private Long userId;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;
    private boolean success;
}
