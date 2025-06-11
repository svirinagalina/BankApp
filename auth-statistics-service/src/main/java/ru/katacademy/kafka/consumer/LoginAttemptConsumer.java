package ru.katacademy.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.accountservice.domain.events.LoginAttemptedEvent;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.domain.repository.LoginAttemptRepository;

@Service
@RequiredArgsConstructor
public class LoginAttemptConsumer {
    private final LoginAttemptRepository loginAttemptRepository;

    @KafkaListener(topics = "login-attempts-topic", groupId = "auth-statistics-group")
    public void consume(LoginAttemptedEvent event) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(event.getUserId());
        attempt.setIp(event.getIp());
        attempt.setUserAgent(event.getUserAgent());
        attempt.setTimestamp(event.getTimestamp());
        attempt.setSuccess(event.isSuccess());

        loginAttemptRepository.save(attempt);
    }
}
