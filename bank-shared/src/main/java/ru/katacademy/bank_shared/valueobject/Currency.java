package ru.katacademy.bank_shared.valueobject;

import java.util.Objects;

/**
 * Объект-значение для представления валюты.
 * <p>
 * Используется для обеспечения типобезопасности при работе с денежными значениями (например, в {@code Money}, {@code Account}).
 * </p>
 *
 * @param code  ISO 4217 код валюты (например, "USD", "EUR", "RUB").
 * @param name  Человекочитаемое название валюты (например, "US Dollar").
 * @param scale Количество знаков после запятой (например, 2 для USD).
 * @author Sheffy
 */
public record Currency(
        String code,
        String name,
        int scale
) {
    /**
     * Валидация и стандартизация данных валюты.
     * <ul>
     *     <li>Проверяет, что {@code code} не null и не пустой.</li>
     *     <li>Приводит {@code code} к верхнему регистру.</li>
     *     <li>Проверяет, что {@code scale} неотрицательный.</li>
     * </ul>
     */
    public Currency {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Код валюты не должен быть пустым");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("Количество знаков после запятой не может быть отрицательным");
        }
        code = code.toUpperCase();
    }

    /**
     * Две валюты считаются равными, если у них одинаковый код.
     *
     * @param o объект для сравнения
     * @return true, если коды валют совпадают
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Currency currency)) {
            return false;
        }
        return Objects.equals(code, currency.code);
    }

    /**
     * Возвращает хэш-код на основе кода валюты.
     *
     * @return хэш-код
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
