package ru.katacademy.auth.infrastructure.mapper;

import org.springframework.stereotype.Component;

import ru.katacademy.auth.application.dto.LoginAttemptDto;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity;
import ru.katacademy.auth.infrastructure.persistence.mapper.LoginAttemptEntityMapper;

/**
 * Маппер для преобразования между сущностью {@link LoginAttempt} и DTO {@link LoginAttemptDto}.
 * <p>
 * Обеспечивает конвертацию между:
 * <ul>
 *   <li>Domain-сущностью ({@link LoginAttempt})</li>
 *   <li>JPA-сущностью ({@link ru.katacademy.auth.infrastructure.persistence.entity.LoginAttemptEntity})</li>
 *   <li>DTO ({@link LoginAttemptDto})</li>
 * </ul>
 * <p><b>Направления преобразований:</b></p>
 * <ol>
 *   <li>Domain ↔ DTO (для REST API)</li>
 *   <li>Domain ↔ JPA (для работы с БД)</li>
 * </ol>
 *
 * @author MihasBatler
 * @see LoginAttempt
 * @see LoginAttemptDto
 */
@Component
public class LoginAttemptMapper {

    /**
     * Преобразует сущность {@link LoginAttempt} в DTO {@link LoginAttemptDto}.
     *
     * @param loginAttempt сущность попытки входа из БД
     * @return DTO с данными о попытке входа
     * @throws IllegalArgumentException если переданный {@code loginAttempt} равен {@code null}
     */
    public LoginAttemptDto toDto(LoginAttempt loginAttempt) {
        if (loginAttempt == null) {
            throw new IllegalArgumentException("LoginAttempt cannot be null");
        }
        return new LoginAttemptDto(
                loginAttempt.getUserId(),
                loginAttempt.getIp(),
                loginAttempt.getUserAgent(),
                loginAttempt.getTimestamp(),
                loginAttempt.isSuccess()
        );
    }

    /**
     * Преобразует domain-сущность в Entity для сохранения в БД.
     *
     * @param domain domain-сущность попытки входа
     * @return соответствующая Entity
     */
    public LoginAttemptEntity  toEntity(LoginAttempt domain) {
        return LoginAttemptEntityMapper.toEntity(domain);
    }

    /**
     * Преобразует JPA-сущность в domain-сущность.
     *
     * @param jpa JPA-сущность из БД
     * @return соответствующая domain-сущность
     */
    public LoginAttempt toDomain(LoginAttemptEntity jpa) {
        return new LoginAttempt(
                jpa.getId(),
                jpa.getUserId(),
                jpa.getTimestamp(),
                jpa.isSuccess(),
                jpa.getIp(),
                jpa.getUserAgent()
        );
    }
}
