package ru.katacademy.infrastructure.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.application.dto.LoginAttemptDto;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.infrastructure.entity.JpaLoginAttempt;

/**
 * Маппер для преобразования между сущностью {@link LoginAttempt} и DTO {@link LoginAttemptDto}.
 * <p>
 * Обеспечивает конвертацию между:
 * <ul>
 *   <li>Domain-сущностью ({@link LoginAttempt})</li>
 *   <li>JPA-сущностью ({@link JpaLoginAttempt})</li>
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
     * Преобразует domain-сущность в JPA-сущность для сохранения в БД.
     *
     * @param domain domain-сущность попытки входа
     * @return соответствующая JPA-сущность
     */
    public JpaLoginAttempt toJpa(LoginAttempt domain) {
        JpaLoginAttempt jpa = new JpaLoginAttempt();
        jpa.setId(domain.getId());
        jpa.setUserId(domain.getUserId());
        jpa.setTimestamp(domain.getTimestamp());
        jpa.setSuccess(domain.isSuccess());
        jpa.setIp(domain.getIp());
        jpa.setUserAgent(domain.getUserAgent());
        return jpa;
    }

    /**
     * Преобразует JPA-сущность в domain-сущность.
     *
     * @param jpa JPA-сущность из БД
     * @return соответствующая domain-сущность
     */
    public LoginAttempt toDomain(JpaLoginAttempt jpa) {
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
