package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.domain.repository.LoginAttemptRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Тесты для LoginAttemptServiceImpl, проверяющие:
 * - Проверка сохранения попытки входа в систему в репозитории
 * - Проверка сохранения текущего времени попытки входа в систему в репозитории
 */

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    @Test
    @DisplayName("Тест 1: Проверка сохранения попытки входа в систему в репозитории")
    void recordLoginAttempt_ShouldSaveEntryToRepository() {
        // Подготовка тестовых данных
        final Long testUserId = 1L;
        final String testEmail = "test@mail.com";
        final String testIp = "192.168.0.1";
        final String testUserAgent = "Mozilla/5.0";
        final boolean testSuccess = true;

        // Вызов тестируемого метода
        loginAttemptService.recordLoginAttempt(testUserId, testEmail, testIp, testUserAgent, testSuccess);

        // Проверяем, что метод save репозитория был вызван ровно один раз
        verify(loginAttemptRepository, times(1)).save(any(LoginAttemptEntry.class));

    }

    @Test
    @DisplayName("Тест 2: Проверка сохранения текущего времени попытки входа в систему в репозитории")
    void recordLoginAttempt_ShouldSetCurrentTimestamp() {
        // Фиксируем текущее время для проверки
        final LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

        // Вызов тестируемого метода
        loginAttemptService.recordLoginAttempt(1L, "test@mail.com", "192.168.0.1", "Mozilla", true);

        // Проверяем, что временная метка установлена корректно
        verify(loginAttemptRepository).save(argThat(entry ->
                entry.getTimestamp() != null &&
                        entry.getTimestamp().isAfter(beforeCall) &&
                        entry.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1))
        ));
    }

}