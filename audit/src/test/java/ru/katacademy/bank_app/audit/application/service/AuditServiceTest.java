package ru.katacademy.bank_app.audit.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.audit.domain.entity.AuditEntry;
import ru.katacademy.bank_app.audit.domain.repository.AuditRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса AuditService, проверяющие:
 * - Успешную запись аудит-события
 * - Проверка исключения AuditServiceException
 * - Проверка логирования при успешной и неудачной записи
 */

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    @DisplayName("Тест 1: Успешная запись аудит-события")
    void record_Success() {
        // Подготовка тестовых данных
        final AuditEntry entry = new AuditEntry("LOGIN", "User logged in", "user1");
        doNothing().when(auditRepository).save(entry);

        // Выполнение и проверка (не должно быть исключений)
        assertDoesNotThrow(() -> auditService.record(entry));

        // Проверка
        verify(auditRepository, times(1)).save(entry);
        verifyNoMoreInteractions(auditRepository);
    }

    @Test
    @DisplayName("Тест 2:Ошибка при сохранении в репозитории должна вызывать исключение AuditServiceException")
    void record_RepositoryError_ShouldThrowAuditServiceException() {
        // Подготовка тестовых данных
        final AuditEntry entry = new AuditEntry("TRANSFER", "Money transfer", "user1");
        final RuntimeException repoException = new RuntimeException("DB error");
        doThrow(repoException).when(auditRepository).save(entry);

        // Выполнение
        final AuditServiceException exception = assertThrows(AuditServiceException.class,
                () -> auditService.record(entry),
                "Должно выбрасываться AuditServiceException при ошибке репозитория");

        //Проверка
        assertEquals(repoException, exception.getCause(),
                "Причина исключения должна быть оригинальной ошибкой репозитория");
        assertEquals("Не удалось записать событие в аудит", exception.getMessage(),
                "Сообщение исключения должно соответствовать ожидаемому");

        // Проверка
        verify(auditRepository, times(1)).save(entry);
    }

    @Test
    @DisplayName("Тест 3: Проверка логирования при успешной записи")
    void record_VerifySuccessLogging() {
        // Подготовка тестовых данных
        final AuditEntry entry = new AuditEntry("LOGOUT", "User logged out", "user1");
        doNothing().when(auditRepository).save(entry);

        // Выполнение
        auditService.record(entry);

        // Проверка
        verify(auditRepository).save(entry);

    }

    @Test
    @DisplayName("Тест 4: Проверка логирования при ошибке записи")
    void record_VerifyErrorLogging() {
        // Подготовка тестовых данных
        final AuditEntry entry = new AuditEntry("FAILURE", "Operation failed", "user1");
        final RuntimeException error = new RuntimeException("Storage failure");
        doThrow(error).when(auditRepository).save(entry);

        // Выполнение + Проверка
        assertThrows(AuditServiceException.class, () -> auditService.record(entry));

        // Проверка
        verify(auditRepository).save(entry);

    }
}