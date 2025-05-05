package ru.katacademy.bank_app.notification.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.notification.application.FraudActionService;
import ru.katacademy.bank_app.shared.event.TransferCompletedEvent;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;

import java.math.BigDecimal;

/**
 * Компонент, отвечающий за обнаружение подозрительных переводов. *
 * Анализирует события перевода из Kafka-топика {@code transfer.completed}.
 * Если сумма перевода превышает установленный лимит,
 * производится блокировка счёта отправителя.</p>
 * <p>
 * Поля:
 * - FRAUD_LIMIT: максимальная сумма перевода
 * - fraudActionService: Сервис для выполнения действий в
 * случае обнаружения мошеннической активности.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionConsumer {
    private static final BigDecimal FRAUD_LIMIT = new BigDecimal("500000");

    private final FraudActionService fraudActionService;

    /**
     * Обрабатывает событие {@link TransferCompletedEvent}, полученное из Kafka.
     * Если сумма перевода превышает {@link #FRAUD_LIMIT}, счёт отправителя блокируется
     * с помощью {@link FraudActionService}.
     *
     * @param event событие завершённого перевода
     */
    @KafkaListener(topics = "transfer.completed", groupId = "fraud")
    public void handleTransferCompleted(TransferCompletedEvent event) {
        final BigDecimal amount = event.amount();
        if (amount.compareTo(FRAUD_LIMIT) > 0) {
            final AccountNumber senderAccountNumber = event.accountFrom().getAccountNumber();
            log.warn("Обнаружена подозрительная операция! " +
                            "Счёт {} заблокирован.",
                    senderAccountNumber);
            fraudActionService.blockAccount(senderAccountNumber);
        }
    }
}
