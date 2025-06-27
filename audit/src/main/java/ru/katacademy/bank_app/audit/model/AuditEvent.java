package ru.katacademy.bank_app.audit.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Событие аудита, содержащее информацию о выполненной бизнес-операции.
 * <p>
 * Используется для логирования ключевых действий пользователя и передачи этих данных в систему аудита.
 * <p>
 * Поля:
 * <ul>
 *   <li>{@code action} — название или тип операции (например, "Создание аккаунта", "Смена пароля")</li>
 *   <li>{@code timestamp} — дата и время выполнения операции</li>
 *   <li>{@code username} — имя пользователя, совершившего операцию</li>
 *   <li>{@code parameters} — параметры или контекст операции в виде строки</li>
 *   <li>{@code status} — статус выполнения операции (например, "SUCCESS" или "FAILURE")</li>
 * </ul>
 * <p>
 * Авторы могут расширять этот класс для добавления дополнительной информации, если потребуется.
 *
 * @author Ваше_имя
 * @since 2025-06-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {

    /**
     * Название или тип операции, которая аудируется.
     */
    private String action;

    /**
     * Временная метка выполнения операции.
     */
    private LocalDateTime timestamp;

    /**
     * Имя пользователя, совершившего операцию.
     */
    private String username;

    /**
     * Параметры операции или дополнительная информация, представленная строкой.
     */
    private String parameters;

    /**
     * Статус выполнения операции (например, "SUCCESS" или "FAILURE").
     */
    private String status;
}
