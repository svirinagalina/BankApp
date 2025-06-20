package ru.katacademy.bank_app.audit.application.service;

import org.junit.jupiter.api.BeforeEach;
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
 * Тестовый класс для {@link AuditService} - сервиса аудита операций.
 * Проверяет корректность записи аудит-записей и обработку ошибок.
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditService auditService;

    private AuditEntry testEntry;

    /**
     * Инициализация тестовых данных перед каждым тестом.
     * Создает тестовую запись аудита для пользователя.
     */
    @BeforeEach
    void setUp() {
        testEntry = new AuditEntry(
                "USER_LOGIN",
                "User with ID 123 logged in",
                "123"
        );
    }

    /**
     * Тест успешной записи аудит-записи.
     * Проверяет что:
     * 1. Сервис корректно вызывает метод save репозитория
     * 2. Запись сохраняется ровно один раз
     */
    @Test
    void record_ShouldSaveAuditEntrySuccessfully() {
        auditService.record(testEntry);
        verify(auditRepository, times(1)).save(testEntry);
    }

    /**
     * Тест обработки ошибки при сохранении.
     * Проверяет что:
     * 1. При ошибке репозитория выбрасывается AuditServiceException
     * 2. Сообщение об ошибке соответствует ожидаемому
     */
    @Test
    void record_ShouldThrowException_WhenSaveFails() {
        doThrow(new RuntimeException("DB error"))
                .when(auditRepository)
                .save(testEntry);

        final AuditServiceException exception = assertThrows(
                AuditServiceException.class,
                () -> auditService.record(testEntry)
        );

        assertEquals("Не удалось записать событие в аудит", exception.getMessage());
    }

    /**
     * Тест обработки системных событий без пользователя.
     * Проверяет что:
     * 1. Записи с null userId корректно сохраняются
     * 2. Репозиторий вызывается с правильными параметрами
     */
    @Test
    void record_ShouldHandleNullUserId() {
        final AuditEntry systemEntry = new AuditEntry(
                "SYSTEM_EVENT",
                "System maintenance started",
                null
        );

        auditService.record(systemEntry);
        verify(auditRepository).save(systemEntry);
        assertNull(systemEntry.getUserId());
    }
}