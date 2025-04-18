package ru.katacademy.bank_app.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.application.command.CreateAccountCommand;
import ru.katacademy.bank_app.account.application.dto.AccountDto;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

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
     * @throws IllegalStateException если возникает ошибка при списании или зачислении средств
     */
    @Transactional
    public void transfer(Account accountFrom, Account accountTo, Money amount) {
        try {
            accountFrom.withdraw(amount);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при списании средств со счёта отправителя", e);
        }

        try {
            accountTo.deposit(amount);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при зачислении средств на счёт получателя", e);
        }
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

        AccountNumber accountNumber = generateAccountNumber();
        Money initialBalance = new Money(BigDecimal.ZERO, cmd.currency());

        Account account = new Account(accountNumber, initialBalance, AccountStatus.ACTIVE);
        accountRepository.save(account);

        return mapToDto(account);
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

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format("Счет с номером %s не найден", accountNumber.value())));

        return mapToDto(account);
    }

    /**
     * Генерирует случайный номер счета.
     *
     * @return строку из 20 цифр, представляющую номер счета
     */
    private AccountNumber generateAccountNumber() {
        StringBuilder number = new StringBuilder(20);
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            number.append(random.nextInt(10));
        }

        return new AccountNumber(number.toString());
    }

    /**
     * Преобразует доменную модель счета в DTO.
     *
     * @param account доменная модель счета (не должна быть null)
     * @return DTO счета
     */
    private AccountDto mapToDto(Account account) {
        return new AccountDto(
                account.getAccountNumber(),
                account.getMoney().currency(),
                account.getMoney().amount(),
                account.getStatus()
        );
    }
}
