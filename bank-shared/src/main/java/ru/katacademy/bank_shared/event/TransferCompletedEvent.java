package ru.katacademy.bank_shared.event;

import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Событие об успешно выполненном переводе между счетами.
 * <p>
 * Содержит все необходимые данные для аудита и обработки перевода.
 * Публикуется в message broker (например, Kafka) после завершения операции.
 * </p>
 *
 * @param eventId           уникальный идентификатор события
 * @param accountNumberFrom счет отправителя
 * @param accountNumberTo   счет получателя
 * @param money            сумма перевода
 * @param localDateTime     время выполнения перевода
 * @author Sheffy
 */
public record TransferCompletedEvent(
        UUID eventId,
        AccountNumber accountNumberFrom,
        AccountNumber accountNumberTo,
        Money money,
        LocalDateTime localDateTime
) {
}