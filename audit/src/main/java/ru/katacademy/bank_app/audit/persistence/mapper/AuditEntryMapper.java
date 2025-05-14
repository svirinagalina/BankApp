package ru.katacademy.bank_app.audit.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.persistence.entity.AuditEntryEntity;

/**
 * Компонент для преобразования объектов {@link AuditEntry} и строковых сообщений Kafka
 * в сущности базы данных и обратно.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
@Component
public class AuditEntryMapper {

    /**
     * Преобразует объект {@link AuditEntry} в {@link AuditEntryEntity} для сохранения в БД.
     *
     * @param auditEntry объект аудита
     * @return сущность, представляющая собой запись аудита для хранения
     */
    public AuditEntryEntity toEntity(AuditEntry auditEntry) {
        final AuditEntryEntity entity = new AuditEntryEntity();
        entity.setEventType(auditEntry.getEventType());
        entity.setMessage(auditEntry.getMessage());
        entity.setUserId(auditEntry.getUserId());
        entity.setTimestamp(auditEntry.getTimestamp().toString());
        return entity;
    }

    /**
     * Преобразует Kafka-сообщение о регистрации пользователя в {@link AuditEntry}.
     *
     * @param message строка-сообщение, полученная из Kafka
     * @return объект аудита с типом события "USER_REGISTER"
     */
    public AuditEntry fromUserRegisterEvent(String message) {
        final String userId = extractField(message);
        return new AuditEntry("USER_REGISTER", message, userId);
    }

    /**
     * Преобразует Kafka-сообщение о завершённом переводе в {@link AuditEntry}.
     *
     * @param message строка-сообщение, полученная из Kafka
     * @return объект аудита с типом события "TRANSFER"
     */
    public AuditEntry fromTransferCompletedEvent(String message) {
        final String userId = extractField(message);
        return new AuditEntry("TRANSFER", message, userId);
    }

    /**
     * Извлекает значение поля userId из строки сообщения.
     * <p>
     * Предполагается формат вида: <code>userId='12345'</code>
     *
     * @param message строка сообщения
     * @return значение userId или {@code null}, если не найдено
     */
    private String extractField(String message) {
        final String pattern = "userId='";
        int start = message.indexOf(pattern);

        if (start == -1) {
            return null;
        }

        start += pattern.length();
        final int end = message.indexOf("'", start);

        if (end == -1) {
            return null;
        }
        
        return message.substring(start, end);
    }
}
