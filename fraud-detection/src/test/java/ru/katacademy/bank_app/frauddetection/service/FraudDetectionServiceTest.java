package ru.katacademy.bank_app.frauddetection.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.frauddetection.client.AccountClient;
import ru.katacademy.bank_app.frauddetection.client.AccountDto;
import ru.katacademy.bank_app.frauddetection.config.FraudDetectionConfig;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.katacademy.bank_shared.valueobject.AccountStatus.ACTIVE;

/**
 * Тестовый класс для {@link FraudDetectionService} - сервиса обнаружения мошеннических операций.
 * Проверяет обработку переводов с разными суммами и граничными условиями.
 */
@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private AccountClient accountClient;
    private FraudDetectionConfig config;
    private FraudDetectionService service;

    @BeforeEach
    void setUp() {
        config = createTestConfig();
        service = new FraudDetectionService(config, accountClient);
    }

    // Тестовые данные
    private final Currency rub = new Currency("RUB", "Russian Ruble", 2);
    private final AccountNumber acc1 = new AccountNumber("12345678901234567890");
    private final AccountNumber acc2 = new AccountNumber("09876543210987654321");

    /**
     * Создает тестовую конфигурацию с порогом 100 000
     */
    private FraudDetectionConfig createTestConfig() {
        final FraudDetectionConfig config = new FraudDetectionConfig();
        config.setSuspiciousAmount(BigDecimal.valueOf(100_000));
        config.setMaxOperationsPerMinute(10);
        config.setMaxOperationsPerHour(50);
        return config;
    }

    /**
     * Создает аккаунт для тестов
     */
    private AccountDto createAccountDto() {
        final Money money = new Money(BigDecimal.valueOf(50_000), rub);
        final AccountDto accountDto = new AccountDto(1L, "12345678901234567890", 1L, money, ACTIVE);
        return accountDto;
    }

    /**
     * Тест обработки null-события.
     * Проверяет что сервис выбрасывает IllegalArgumentException если event == null.
     */
    @Test
    void analyze_ShouldThrowException_WhenEventIsNull() {
        final AccountDto accountDto = createAccountDto();

        assertThrows(IllegalArgumentException.class,
                () -> service.analyze(null, accountDto));
    }

    /**
     * Тест обработки null-события.
     * Проверяет что сервис выбрасывает IllegalArgumentException если accountDto == null.
     */
    @Test
    void analyze_ShouldThrowException_WhenAccountDtoIsNull() {
        final Money normalAmount = new Money(BigDecimal.valueOf(50_000), rub);
        final var event = new TransferCompletedEvent(
                UUID.randomUUID(),
                acc1,
                acc2,
                normalAmount,
                LocalDateTime.now()
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.analyze(event, null));
    }

    /**
     * Тест обработки обычного перевода (сумма ниже порога подозрительности).
     * Проверяет что сервис корректно обрабатывает перевод на 50 000 RUB.
     */
    @Test
    void analyze_ShouldProcessNormalTransfer() {
        final Money normalAmount = new Money(BigDecimal.valueOf(50_000), rub);
        final var event = new TransferCompletedEvent(
                UUID.randomUUID(),  // случайный ID операции
                acc1,              // счет отправителя
                acc2,              // счет получателя
                normalAmount,      // сумма перевода
                LocalDateTime.now() // текущая дата и время
        );
        final AccountDto accountDto = createAccountDto();
        service.analyze(event, accountDto);

        // Проверяем, что блокировка не вызывалась
        verify(accountClient, never()).blockById(anyLong());
    }

    /**
     * Тест обработки подозрительного перевода (сумма выше порога).
     * Проверяет обработку перевода на 150 000 RUB.
     */
    @Test
    void analyze_ShouldProcessSuspiciousTransfer() {
        final Money suspiciousAmount = new Money(BigDecimal.valueOf(150_000), rub);
        final var event = new TransferCompletedEvent(
                UUID.randomUUID(),
                acc1,
                acc2,
                suspiciousAmount,
                LocalDateTime.now()
        );

        final AccountDto accountDto = createAccountDto();
        service.analyze(event, accountDto);

        // Проверяем, что блокировка вызвалась
        verify(accountClient).blockById(1L);
    }

    /**
     * Тест обработки перевода с суммой ровно на пороге (100 000 RUB).
     * Проверяет граничное условие работы сервиса.
     */
    @Test
    void analyze_ShouldProcessTransferWithExactThreshold() {
        final Money thresholdAmount = new Money(BigDecimal.valueOf(100_000), rub);
        final var event = new TransferCompletedEvent(
                UUID.randomUUID(),
                acc1,
                acc2,
                thresholdAmount,
                LocalDateTime.now()
        );

        final AccountDto accountDto = createAccountDto();
        service.analyze(event, accountDto);

        verify(accountClient, never()).blockById(anyLong());
    }
}