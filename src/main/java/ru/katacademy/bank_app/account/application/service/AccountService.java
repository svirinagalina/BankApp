package ru.katacademy.bank_app.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.application.command.CreateAccountCommand;
import ru.katacademy.bank_app.account.application.dto.AccountDto;
import ru.katacademy.bank_app.account.application.mapper.AccountMapper;
import ru.katacademy.bank_app.account.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.account.infrastructure.persistence.mapper.AccountEntityMapper;
import ru.katacademy.bank_app.notification.application.NotificationService;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;
import ru.katacademy.bank_shared.exception.BusinessRuleViolationException;
import ru.katacademy.bank_shared.exception.CurrencyMismatchException;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.valueobject.Money;


import java.time.LocalDateTime;
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
    private final TransferValidator validator;

    /**
     * Переводит денежные средства от одного аккаунта к другому.
     * Сначала выполняется списание средств с аккаунта отправителя,
     * затем зачисление на аккаунт получателя.
     * <p>
     * Метод помечен как {@code @Transactional}, чтобы, обеспечить атомарность операции:
     * если одна из операций завершится с ошибкой, изменения будут откатаны.
     * </p>
     *
     * @param from   аккаунт отправителя
     * @param to     аккаунт получателя
     * @param amount сумма перевода (объект {@link Money})
     * @throws IllegalArgumentException       если {@code from} или {@code to} равны {@code null}
     * @throws BusinessRuleViolationException если аккаунт отправителя-получателя не активен,
     *                                        валюты не совпадают или сумма депозита меньше или сумма списания больше или ровна нулю
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    @Transactional
    public void transfer(AccountNumber from, AccountNumber to, Money amount) throws AccountNotFoundException {

        if (from == null) {
            throw new IllegalArgumentException("Номер счёта списания не может быть null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Номер счёта зачисления не может быть null");
        }

        final Account accountFrom = getAccountByAccountNumber(from);
        final Account accountTo = getAccountByAccountNumber(to);

        validator.validateTransferTo(accountFrom, accountTo, amount);
        accountFrom.withdraw(amount);
        accountTo.deposit(amount);

        accountRepository.save(AccountEntityMapper.toAccountEntity(accountFrom));
        accountRepository.save(AccountEntityMapper.toAccountEntity(accountTo));

        notificationService.sendTransferNotification(accountFrom, accountTo, amount);

        // Создаем событие о завершении перевода
        final TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                from,
                to,
                amount,
                LocalDateTime.now()
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

        final Account account = Account.newAccount(accountNumber, initialBalance, AccountStatus.ACTIVE);
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

    private Account getAccountByAccountNumber(AccountNumber accountNumber) {
        return AccountEntityMapper.toAccount(accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Счёт не найден")));
    }

    /**
     * Блокирует счет (Account) по его номеру.
     *
     * @param accountNumber номер аккаунта, который необходимо заблокировать
     * @throws AccountNotFoundException если аккаунт с указанным номером не найден
     */
    public void blockAccountByNumber(AccountNumber accountNumber) {
        final AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber.toString()));

        final Account account = AccountEntityMapper.toAccount(accountEntity);
        account.blockAccount();
        accountRepository.save(AccountEntityMapper.toAccountEntity(account));
    }
}
