package ru.katacademy.bank_app.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.application.dto.AccountDto;
import ru.katacademy.bank_app.account.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.notification.application.NotificationService;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.event.TransferComplitedEvent;
import ru.katacademy.bank_app.shared.valueobject.Money;

import java.time.Instant;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сервис для выполнения операций со счетами.
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final TransferEventPublisher eventPublisher;



    /**
     * Переводит денежные средства от одного аккаунта к другому.
     * Сначала выполняется списание средств с аккаунта отправителя,
     * затем зачисление на аккаунт получателя.
     * <p>
     * Метод помечен как {@code @Transactional}, чтобы, обеспечить атомарность операции:
     * если одна из операций завершится с ошибкой, изменения будут откатаны.
     * </p>
     *
     * @param from аккаунт отправителя
     * @param to   аккаунт получателя
     * @param amount      сумма перевода (объект {@link Money})
     * @throws IllegalStateException если возникает ошибка при списании или зачислении средств
     */
    @Transactional
    public void transfer(AccountNumber from, AccountNumber to, Money amount) {
        final AccountEntity entityFrom = accountRepository.findByAccountNumber(from)
                .orElseThrow(() -> new IllegalArgumentException("счёт отправителя не найден"));
        final Account accountFrom = AccountEntityMapper.toAccount(entityFrom);

        final AccountEntity entityTo = accountRepository.findByAccountNumber(to)
                .orElseThrow(() -> new IllegalArgumentException("счёт получателя не найден"));
        final Account accountTo = AccountEntityMapper.toAccount(entityTo);

        accountFrom.withdraw(amount);
        accountTo.deposit(amount);

        accountRepository.save(AccountEntityMapper.toAccountEntity(accountFrom));
        accountRepository.save(AccountEntityMapper.toAccountEntity(accountTo));

        notificationService.sendTransferNotification(accountFrom, accountTo, amount);

        notificationService.sendTransferNotification(accountFrom, accountTo, amount);

        // Создаем и публикуем событие о завершении перевода
        TransferComplitedEvent event = new TransferComplitedEvent(
                UUID.randomUUID(),
                accountFrom,
                accountTo,
                amount.amount(),
                amount.currency(),
                Instant.now()
        );

        // публикация события в Kafka
        eventPublisher.publish(event);
    }

    /**
     * Создает новый банковский счет.
     *
     * @param cmd команда создания счета (не должна быть null)
     * @return DTO созданного счета
     * @throws IllegalArgumentException если команда или её параметры невалидны
     */
    public AccountDto createAccount(CreateAccountCommand cmd) {
        Objects.requireNonNull(cmd, "Команда создания счета не может быть null");
        Objects.requireNonNull(cmd.currency(), "Валюта счета не может быть null");

        final AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        final Money initialBalance = new Money(BigDecimal.ZERO, cmd.currency());

        final Account account = new Account(accountNumber, initialBalance, AccountStatus.ACTIVE);
        final AccountEntity accountEntity = AccountEntityMapper.toAccountEntity(account);
        accountRepository.save(accountEntity);

        return AccountMapper.toAccountDto(account);
    }

    /**
     * Получает информацию о счете по его номеру.
     *
     * @param accountNumber номер счета (не должен быть null)
     * @return DTO с информацией о счете
     * @throws AccountNotFoundException если счет не найден
     * @throws IllegalArgumentException если accountNumber == null
     */
    public AccountDto getByAccountNumber(AccountNumber accountNumber)
            throws AccountNotFoundException {
        Objects.requireNonNull(accountNumber, "Номер счета не может быть null");

        final AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Счет с номером %s не найден", accountNumber.value())));
        final Account account = AccountEntityMapper.toAccount(accountEntity);
        return AccountMapper.toAccountDto(account);
    }
}
