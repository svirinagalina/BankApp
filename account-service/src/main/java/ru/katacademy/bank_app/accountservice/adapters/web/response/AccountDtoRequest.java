package ru.katacademy.bank_app.accountservice.adapters.web.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

@Setter
@Getter
public class AccountDtoRequest {

    /**
     * Пользователь, для которого создается счет.
     * Не может быть null.
     */
    @NotNull(message = "Пользователь не может быть пустым")
    @Valid // Важно! Для валидации вложенных объектов
    private UserEntity user;

    /**
     * Номер банковского счета.
     * Не может быть null.
     */
    @NotNull(message = "Номер счета не может быть пустым")
    @Valid // Важно! Для валидации вложенных объектов
    private AccountNumber number;

    /**
     * Начальный баланс счета.
     * Не может быть null, должен быть >= 0.
     */
    @NotNull(message = "Начальный баланс не может быть пустым")
    @Valid // Важно! Для валидации вложенных объектов
    @DecimalMin(value = "0.0", message = "Начальный баланс не может быть отрицательным")
    private Money initialBalance;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public AccountNumber getNumber() {
        return number;
    }

    public void setNumber(AccountNumber number) {
        this.number = number;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Money initialBalance) {
        this.initialBalance = initialBalance;
    }
}