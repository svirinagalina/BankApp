package ru.katacademy.bank_shared.exception;

/**
 * Перечисление кодов бизнес-ошибок.
 * Используются для указания причины нарушения бизнес-правил.
 */
public enum BusinessErrorCode {
    /**
     * Не активный аккаунт.
     */
    INACTIVE_ACCOUNT,
    /**
     * Несовпадение валют.
     */
    CURRENCY_MISMATCH,
    /**
     * Недостаточно средств.
     */
    INSUFFICIENT_FUNDS,
    /**
     * Недопустимая сумма.
     */
    INVALID_AMOUNT,
}