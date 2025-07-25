package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.domain.events.LoginAttemptedEvent;
import ru.katacademy.bank_app.accountservice.application.port.out.LoginAttemptRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для {@link LoginAttemptServiceImpl} - сервиса записи попыток входа.
 * Проверяет корректность работы метода записи попыток аутентификации.
 * Проверяет что:
 * 1. Сервис корректно вызывает метод save репозитория
 * 2. Создает запись о попытке входа с правильными параметрами
 * 3. Отправляет событие о попытке входа в Kafka-топик
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @Mock
    private KafkaTemplate<String, LoginAttemptedEvent> kafkaTemplate;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    /**
     * Тест записи попытки входа.
     * Проверяет что:
     * 1. Сервис корректно вызывает метод save репозитория
     * 2. Передает объект попытки входа в репозиторий
     * 3. Отправляет событие в Kafka
     */
    @Test
    void recordLoginAttempt_ShouldRegisterLoginAttempt() {
        // Тестовые данные
        final Long userId = 1L;
        final String email = "user@mail.com";
        final String ip = "192.168.1.1";
        final String userAgent = "Chrome";
        final boolean success = true;

        loginAttemptService.recordLoginAttempt(userId, email, ip, userAgent, success);
        verify(loginAttemptRepository).save(any(LoginAttemptEntry.class));
        verify(kafkaTemplate).send(eq("login-attempts-topic"), any(LoginAttemptedEvent.class));
    }
}