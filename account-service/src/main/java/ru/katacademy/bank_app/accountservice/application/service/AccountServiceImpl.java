package ru.katacademy.bank_app.accountservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.application.port.out.AccountRepository;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.accountservice.domain.mapper.AccountMapper;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;

import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.repository.AccountJpaRepository;
import ru.katacademy.bank_shared.exception.AccountNotFoundExceptionResolver;
import ru.katacademy.bank_shared.exception.MaxAccountsExceededException;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.time.LocalDateTime;

/**
 * Реализация сервиса банковских аккаунтов.
 * Обрабатывает создание, блокировку и получение аккаунта через репозиторий.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountJpaRepository accountJpaRepository, AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountJpaRepository = accountJpaRepository;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
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
}
