package ru.katacademy.bank_app.frauddetection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;

import java.math.BigDecimal;

/**
 * Сервис для выявления мошеннических операций.
 * <p>
 * Анализирует транзакции по заданным правилам и логирует подозрительную активность.
 * </p>
 * @author Sheffy
 */
@Service
@Slf4j
public class FraudDetectionService {

    /**
     * Пороговая сумма для подозрительных операций (100 000)
     */
    private static final BigDecimal SUSPICIOUS_AMOUNT = BigDecimal.valueOf(100_000);

    /**
     * Анализирует операцию перевода на признаки мошенничества.
     *
     * @param event событие перевода (не null)
     * @throws IllegalArgumentException если event == null
     */
    public void analyze(TransferCompletedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Событие перевода не может быть null");
        }


        /**
         * Проверяет сумму на превышение порогового значения.
         */
        if (event.money().amount().compareTo(SUSPICIOUS_AMOUNT) > 0) {
            log.warn("Подозрительная активность! Перевод на сумму - {}", event.money().amount());
            // TODO: В будущем отправлять команду на блокировку аккаунта
        }
    }
}
