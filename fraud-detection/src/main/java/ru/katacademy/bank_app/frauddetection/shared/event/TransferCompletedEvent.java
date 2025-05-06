package ru.katacademy.bank_app.frauddetection.shared.event;

import ru.katacademy.bank_app.frauddetection.account.domain.entity.Account;
import ru.katacademy.bank_app.frauddetection.shared.valueobject.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Событие об успешно выполненном переводе между счетами.
 * <p>
 * Содержит все необходимые данные для аудита и обработки перевода.
 * Публикуется в message broker (например, Kafka) после завершения операции.
 * </p>
 *
 * @param eventId     уникальный идентификатор события
 * @param accountFrom счет отправителя
 * @param accountTo   счет получателя
 * @param amount      сумма перевода
 * @param currency    валюта перевода
 * @param timestamp   время выполнения перевода
 * @author Sheffy
 */
public record TransferCompletedEvent(
        UUID eventId,
        Account accountFrom,
        Account accountTo,
        BigDecimal amount,
        Currency currency,
        Instant timestamp
) {
}
