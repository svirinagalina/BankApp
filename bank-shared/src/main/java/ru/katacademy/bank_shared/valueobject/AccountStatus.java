package ru.katacademy.bank_shared.valueobject;

public enum AccountStatus {
    /**
     * Счет активен и доступен для всех операций.
     */
    ACTIVE,

    /**
     * Счет заблокирован. Операции по счету временно недоступны.
     */
    BLOCKED,

    /**
     * Счет закрыт. Дальнейшие операции невозможны.
     */
    CLOSE
}

