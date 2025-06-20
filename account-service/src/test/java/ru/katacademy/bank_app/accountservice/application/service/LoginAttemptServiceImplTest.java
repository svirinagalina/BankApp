package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.accountservice.domain.repository.LoginAttemptRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для {@link LoginAttemptServiceImpl} - сервиса записи попыток входа.
 * Проверяет корректность работы метода записи попыток аутентификации.
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    /**
     * Тест записи попытки входа.
     * Проверяет что:
     * 1. Сервис корректно вызывает метод save репозитория
     * 2. Передает объект попытки входа в репозиторий
     */
    @Test
    void recordLoginAttempt_ShouldRegisterLoginAttempt() {
        // Вызываем тестируемый метод с тестовыми данными
        loginAttemptService.recordLoginAttempt(
                1L,                    // ID пользователя
                "user@mail.com",    // Email
                "192.168.1.1",         // IP-адрес
                "Chrome",              // User-Agent
                true                   // Статус успешности
        );

        // Проверяем взаимодействие с репозиторием
        verify(loginAttemptRepository).save(any());
    }
}