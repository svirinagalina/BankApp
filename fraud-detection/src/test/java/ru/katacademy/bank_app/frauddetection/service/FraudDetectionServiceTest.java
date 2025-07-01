package ru.katacademy.bank_app.frauddetection.service;

import org.junit.jupiter.api.Test;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тестовый класс для {@link FraudDetectionService} - сервиса обнаружения мошеннических операций.
 * Проверяет обработку переводов с разными суммами и граничными условиями.
 */
class FraudDetectionServiceTest {

    // Тестируемый сервис
    private final FraudDetectionService service = new FraudDetectionService();

    // Тестовые данные
    private final Currency rub = new Currency("RUB", "Russian Ruble", 2);
    private final AccountNumber acc1 = new AccountNumber("12345678901234567890");
    private final AccountNumber acc2 = new AccountNumber("09876543210987654321");

    /**
     * Тест обработки null-события.
     * Проверяет что сервис выбрасывает IllegalArgumentException при получении null.
     */
    @Test
    void analyze_ShouldThrowException_WhenEventIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.analyze(null));
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

        assertDoesNotThrow(() -> service.analyze(event));
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

        assertDoesNotThrow(() -> service.analyze(event));
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

        assertDoesNotThrow(() -> service.analyze(event));
    }
}