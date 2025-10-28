package ru.katacademy.bank_app.frauddetection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.frauddetection.client.AccountClient;
import ru.katacademy.bank_app.frauddetection.client.AccountDto;
import ru.katacademy.bank_app.frauddetection.config.FraudDetectionConfig;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;

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

    private final FraudDetectionConfig fdConfig;
    private final AccountClient  accountClient;

    @Autowired
    public FraudDetectionService(FraudDetectionConfig fdConfig, AccountClient accountClient) {
        this.fdConfig = new FraudDetectionConfig(fdConfig);
        this.accountClient = accountClient;
    }

    /**
     * Анализирует операцию перевода на признаки мошенничества.
     *
     * @param event событие перевода (не null)
     * @param accountDto событие перевода (не null)
     * @throws IllegalArgumentException если event == null
     * @throws IllegalArgumentException если accountDto == null
     */
    public void analyze(TransferCompletedEvent event, AccountDto accountDto) {
        if (event == null) {
            throw new IllegalArgumentException("Событие перевода не может быть null");
        }
        if (accountDto == null) {
            throw new IllegalArgumentException("AccountDto не может быть null");
        }

        /**
         * Проверяет сумму на превышение порогового значения.
         */
        if (event.money().amount().compareTo(fdConfig.getSuspiciousAmount()) > 0) {
            log.warn("Подозрительная активность! Перевод на сумму - {}", event.money().amount());
            log.info("Аккаунт {} заблокирован из-за подозрительной транзакции.", accountDto.accountNumber());

            accountClient.blockById(accountDto.id());
        }

    }
}
