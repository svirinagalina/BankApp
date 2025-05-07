package ru.katacademy.bank_app.account.application.command;

import ru.katacademy.bank_shared.valueobject.Currency;

/**
 * Команда для создания нового банковского счета.
 * <p>
 * Содержит минимально необходимые данные для инициализации счета.
 * Используется в слое приложения для передачи параметров создания счета.
 * </p>
 *
 * @param userId   идентификатор пользователя-владельца счета
 * @param currency валюта создаваемого счета
 * @author Sheffy
 */
public record CreateAccountCommand(
        Long userId,
        Currency currency
) {
}