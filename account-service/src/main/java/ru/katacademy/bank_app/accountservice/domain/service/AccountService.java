package ru.katacademy.bank_app.accountservice.domain.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
    void transferMoney(Long fromAccountId, Long toAccountId, Money amount);

    AccountEntity createAccount(@NotNull(message = "ID пользователя не может быть пустым") @Positive(message = "ID пользователя должен быть положительным числом") Long userId, @NotBlank(message = "Номер счета не может быть пустым") @Pattern(regexp = "^\\d{20}$", message = "Номер счета должен содержать ровно 20 цифр") String accountNumber, @NotNull(message = "Начальный баланс не может быть пустым") @DecimalMin(value = "0.0", inclusive = true, message = "Начальный баланс не может быть отрицательным") @DecimalMax(value = "1000000.0", message = "Начальный баланс слишком велик") BigDecimal initialBalance, @NotBlank(message = "Валюта счета не может быть пустой") @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть в формате ISO (3 заглавные буквы)") String currency);
}

