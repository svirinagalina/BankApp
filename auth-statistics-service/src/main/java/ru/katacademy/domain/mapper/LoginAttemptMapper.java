package ru.katacademy.domain.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.application.dto.LoginAttemptDto;
import ru.katacademy.domain.entity.LoginAttempt;

/**
 * Маппер для преобразования между сущностью {@link LoginAttempt} и DTO {@link LoginAttemptDto}.
 * <p>
 * Используется для конвертации данных:
 * <ul>
 *   <li>Из Entity в DTO при передаче данных через REST API</li>
 *   <li>Из DTO в Entity при сохранении данных из Kafka в БД</li>
 * </ul>
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
        return new LoginAttemptDto(
                loginAttempt.getUserId(),
                loginAttempt.getIp(),
                loginAttempt.getUserAgent(),
                loginAttempt.getTimestamp(),
                loginAttempt.isSuccess()
        );
    }
}
