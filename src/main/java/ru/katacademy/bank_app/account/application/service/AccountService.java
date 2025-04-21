package ru.katacademy.bank_app.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.application.command.CreateAccountCommand;
import ru.katacademy.bank_app.account.application.dto.AccountDto;
import ru.katacademy.bank_app.account.application.mapper.AccountMapper;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.account.infrastructure.persistence.mapper.AccountEntityMapper;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.exception.AccountInactiveException;
import ru.katacademy.bank_app.shared.exception.InsufficientFundsException;
import ru.katacademy.bank_app.shared.valueobject.Money;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сервис для выполнения операций со счетами.
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;


    /**
     * Переводит денежные средства от одного аккаунта к другому.
     * Сначала выполняется списание средств с аккаунта отправителя,
     * затем зачисление на аккаунт получателя.
     * <p>
     * Метод помечен как {@code @Transactional}, чтобы, обеспечить атомарность операции:
     * если одна из операций завершится с ошибкой, изменения будут откатаны.
     * </p>
     *
     * @param accountFrom аккаунт отправителя
     * @param accountTo   аккаунт получателя
     * @param amount      сумма перевода (объект {@link Money})
     * @throws AccountInactiveException   если аккаунт одной из сторон не активен
     * @throws InsufficientFundsException если на счёте отправителя недостаточно денег
     */
    @Transactional
    public void transfer(Account accountFrom, Account accountTo, Money amount) {
        if (!accountFrom.isActive()) {
            throw new AccountInactiveException("Аккаунт отправителя неактивен");
        }
        if (!accountTo.isActive()) {
            throw new AccountInactiveException("Аккаунт получателя неактивен");
        }
        accountFrom.withdraw(amount);
        accountTo.deposit(amount);
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

        AccountNumber accountNumber = AccountNumber.generateAccountNumber();
        Money initialBalance = new Money(BigDecimal.ZERO, cmd.currency());

        Account account = new Account(accountNumber, initialBalance, AccountStatus.ACTIVE);
        accountRepository.save(account);

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

        AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Счет с номером %s не найден", accountNumber.value())));
        Account account = AccountEntityMapper.toAccount(accountEntity);
        return AccountMapper.toAccountDto(account);
    }
}
