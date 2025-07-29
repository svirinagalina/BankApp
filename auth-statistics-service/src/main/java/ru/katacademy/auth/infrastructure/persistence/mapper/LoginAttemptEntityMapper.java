package ru.katacademy.auth.infrastructure.persistence.mapper;

import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity;

/**
 * Утилитный класс для преобразования между {@link LoginAttempt} и {@link LoginAttemptEntity}.
 * <p>Выполняет «антикоррупционный слой» между доменом и JPA‑сущностями.</p>
 */
public class LoginAttemptEntityMapper {

    /**
     * Преобразует доменную модель {@link LoginAttempt} в JPA‑сущность {@link LoginAttemptEntity}.
     *
     * @param d доменный объект попытки входа
     * @return новая JPA‑сущность для сохранения в БД
     */
    public static LoginAttemptEntity toEntity(LoginAttempt d) {
        LoginAttemptEntity e = new LoginAttemptEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setTimestamp(d.getTimestamp());
        e.setSuccess(d.isSuccess());
        e.setIp(d.getIp());
        e.setUserAgent(d.getUserAgent());
        return e;
    }

    /**
     * Преобразует JPA‑сущность {@link LoginAttemptEntity} в доменную модель {@link LoginAttempt}.
     *
     * @param e JPA‑сущность из БД
     * @return новый доменный объект с теми же данными
     */
    public static LoginAttempt toDomain(LoginAttemptEntity e) {
        return new LoginAttempt(
                e.getId(),
                e.getUserId(),
                e.getTimestamp(),
                e.isSuccess(),
                e.getIp(),
                e.getUserAgent()
        );
    }
}
