package ru.katacademy.bank_app.accountservice.adapters.web.util;

import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;
import ru.katacademy.bank_shared.valueobject.Currency;

import java.math.BigDecimal;
import java.util.Map;

public final class WebLayerParser {

    private WebLayerParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Простое маппинг кодов валют на их scale
    private static final Map<String, Integer> CURRENCY_SCALES = Map.of(
            "RUB", 2,
            "USD", 2,
            "EUR", 2,
            "JPY", 0,
            "CNY", 2
    );

    // Названия валют
    private static final Map<String, String> CURRENCY_NAMES = Map.of(
            "RUB", "Russian Ruble",
            "USD", "US Dollar",
            "EUR", "Euro",
            "JPY", "Japanese Yen",
            "CNY", "Chinese Yuan"
    );

    public static AccountNumber parseAccountNumber(String accountNumberStr) {
        try {
            return new AccountNumber(accountNumberStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный номер счета: " + accountNumberStr);
        }
    }

    public static Money parseMoney(BigDecimal amount, String currencyCode) {
        try {
            final Currency currency = createCurrency(currencyCode);
            return new Money(amount, currency);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный код валюты: " + currencyCode);
        }
    }

    private static Currency createCurrency(String currencyCode) {
        final String upperCode = currencyCode.toUpperCase();

        // Получаем scale (по умолчанию 2)
        final int scale = CURRENCY_SCALES.getOrDefault(upperCode, 2);

        // Получаем name (по умолчанию код)
        final String name = CURRENCY_NAMES.getOrDefault(upperCode, upperCode);

        return new Currency(upperCode, name, scale);
    }

    public static Long parseLongId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат ID: " + idStr);
        }
    }
}