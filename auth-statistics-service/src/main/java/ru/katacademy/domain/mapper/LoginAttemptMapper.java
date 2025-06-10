package ru.katacademy.domain.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.application.dto.LoginAttemptDto;
import ru.katacademy.domain.entity.LoginAttempt;

@Component
public class LoginAttemptMapper {
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
