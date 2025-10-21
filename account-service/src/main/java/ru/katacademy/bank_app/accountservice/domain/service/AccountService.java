package ru.katacademy.bank_app.accountservice.domain.service;

import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Сервис для работы с аккаунтами.
 * Содержит бизнес-логику создания и получения аккаунтов.
 */
public interface AccountService {
    AccountEntity createAccount(UserEntity user, AccountNumber accountNumber, Money initialBalance);
    AccountDto getById(Long id);
    void blockAccountById(Long id);
}
