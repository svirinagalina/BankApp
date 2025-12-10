package ru.katacademy.bank_app.accountservice.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.application.port.out.AccountRepository;
import ru.katacademy.bank_app.accountservice.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.accountservice.domain.mapper.AccountMapper;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.repository.AccountJpaRepository;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;
import ru.katacademy.bank_shared.exception.AccountNotFoundExceptionResolver;
import ru.katacademy.bank_shared.exception.AccountStatusException;
import ru.katacademy.bank_shared.exception.InsufficientFundsException;
import ru.katacademy.bank_shared.exception.MaxAccountsExceededException;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Реализация сервиса банковских аккаунтов.
 * Обрабатывает создание, блокировку и получение аккаунта через репозиторий.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransferEventPublisher transferEventPublisher;

    public AccountServiceImpl(AccountJpaRepository accountJpaRepository, AccountRepository accountRepository,
                              AccountMapper accountMapper, TransferEventPublisher transferEventPublisher) {
        this.accountJpaRepository = accountJpaRepository;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.transferEventPublisher = transferEventPublisher;
    }

    @Transactional
    @Override
    public AccountEntity createAccount(UserEntity user, AccountNumber accountNumber, Money initialBalance) {
        final long count = accountJpaRepository.countByUserId(user.getId());
        if (count >= 5) {
            throw new MaxAccountsExceededException("User " + user + " уже имеет " + count + " аккаунтов");
        }

        final AccountEntity account = new AccountEntity(accountNumber, user, initialBalance, AccountStatus.ACTIVE, LocalDateTime.now());
        return accountJpaRepository.save(account);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDto getById(Long id) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundExceptionResolver("Аккаунт с id " + id + " не найден"));

        return accountMapper.toDto(account);
    }

    @Transactional
    @Override
    public void blockAccountById(Long id) {
        final AccountEntity account = accountJpaRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundExceptionResolver("Аккаунт с id " + id + " не найден"));
        account.setStatus(AccountStatus.BLOCKED);
        accountJpaRepository.save(account);
    }

    @Transactional
    @Override
    public void transferMoney(Long fromAccountId, Long toAccountId, Money amount) throws InsufficientFundsException, AccountNotFoundException {
        final Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Аккаунт с ID " + fromAccountId + " не найден"));
        final Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Аккаунт с ID " + toAccountId + " не найден"));

        if (fromAccount.getBalance().amount().compareTo(amount.amount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на счете отправителя");
        }

        if (fromAccount.getStatus() != AccountStatus.ACTIVE || toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Аккаунт не активен");
        }

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Сохраняем обновленные счета в базе данных
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        final TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                amount,
                LocalDateTime.now()
        );
        transferEventPublisher.publish(event); // Публикуем событие в Kafka
    }

}
