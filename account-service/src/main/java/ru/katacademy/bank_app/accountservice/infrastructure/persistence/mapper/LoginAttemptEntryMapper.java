package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;

import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.LoginAttemptEntryEntity;

/**
 * Маппер между {@link LoginAttemptEntry} и {@link LoginAttemptEntryEntity}.
 */
public class LoginAttemptEntryMapper {

    /**
     * Преобразует доменную модель в JPA‑сущность.
     *
     * @param d доменный объект
     * @return JPA‑сущность для сохранения
     */
    public static LoginAttemptEntryEntity toEntity(LoginAttemptEntry d) {
        final LoginAttemptEntryEntity e = new LoginAttemptEntryEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setEmail(d.getEmail());
        e.setIp(d.getIp());
        e.setUserAgent(d.getUserAgent());
        e.setTimestamp(d.getTimestamp());
        e.setSuccess(d.isSuccess());
        return e;
    }

    /**
     * Преобразует JPA‑сущность обратно в доменную модель.
     *
     * @param e JPA‑сущность из БД
     * @return доменный объект
     */
    public static LoginAttemptEntry toDomain(LoginAttemptEntryEntity e) {
        final LoginAttemptEntry d = new LoginAttemptEntry();
        d.setId(e.getId());
        d.setUserId(e.getUserId());
        d.setEmail(e.getEmail());
        d.setIp(e.getIp());
        d.setUserAgent(e.getUserAgent());
        d.setTimestamp(e.getTimestamp());
        d.setSuccess(e.isSuccess());
        return d;
    }
}
