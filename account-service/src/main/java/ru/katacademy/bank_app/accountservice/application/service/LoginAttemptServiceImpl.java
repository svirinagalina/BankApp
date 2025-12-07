package ru.katacademy.bank_app.accountservice.application.service;

import org.apache.kafka.common.KafkaException;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.domain.events.LoginAttemptedEvent;
import ru.katacademy.bank_app.accountservice.application.port.out.LoginAttemptRepository;
import ru.katacademy.bank_app.accountservice.domain.service.LoginAttemptService;
import ru.katacademy.bank_shared.aspect.RetryableOperation;


import java.time.LocalDateTime;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;
    private final KafkaTemplate<String, LoginAttemptedEvent> kafkaTemplate;

    public LoginAttemptServiceImpl(LoginAttemptRepository loginAttemptRepository, KafkaTemplate<String, LoginAttemptedEvent> kafkaTemplate) {
        this.loginAttemptRepository = loginAttemptRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Регистрирует попытку входа пользователя в систему.
     * <p>
     * Этот метод создает запись о попытке входа, включая информацию о пользователе,
     * его IP-адресе, типе пользовательского агента и статусе успеха. Также автоматически
     * присваивается текущая временная метка для каждой попытки входа.
     * Метод создает объект события {@link LoginAttemptedEvent} с переданными параметрами.
     * Отправляет событие о попытке входа в Kafka-топик {@code login-attempts-topic}
     * </p>
     *
     * @param userId    Идентификатор пользователя, пытающегося войти в систему.
     * @param email     Электронная почта пользователя, связанная с попыткой входа.
     * @param ip        IP-адрес, с которого была осуществлена попытка входа.
     * @param userAgent Информация о браузере или устройстве пользователя,
     *                  отправленная в заголовках HTTP.
     * @param success   Флаг, указывающий, была ли попытка входа успешной
     *                  (true) или нет (false).
     *                  <p>
     *                  Автор: Колпаков А.С..
     *                  Дата: 2025-05-05
     */
    @Override
    @RetryableOperation(
            maxAttempts = 4,
            backoffDelayMs = 1000,
            retryOn = { DataAccessException.class, KafkaException.class }
    )
    public void recordLoginAttempt(Long userId, String email, String ip, String userAgent, boolean success) {
        final LoginAttemptEntry entry = new LoginAttemptEntry(
                userId,
                email,
                ip,
                userAgent,
                LocalDateTime.now(),
                success
        );
        loginAttemptRepository.save(entry);
        final LoginAttemptedEvent event = new LoginAttemptedEvent(
                userId,
                ip,
                userAgent,
                LocalDateTime.now(),
                success
        );
        kafkaTemplate.send("login-attempts-topic", event);
    }
}
