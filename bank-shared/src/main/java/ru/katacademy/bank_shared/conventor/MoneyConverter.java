package ru.katacademy.bank_shared.conventor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;

/**
 * JPA-конвертер для value object'а {@link Money}.
 * Используется для автоматического преобразования Money в строку при сохранении в БД
 * и восстановления объекта Money из строки при загрузке.
 */
@Converter
public class MoneyConverter implements AttributeConverter<Money, String> {

    private final CurrencyConverter currencyConverter = new CurrencyConverter();

    /**
     * Преобразует объект {@link Money} в строку для хранения в БД.
     * Формат: "сумма|валюта", где валюта сериализована {@link CurrencyConverter}.
     *
     * @param money объект Money или null
     * @return строковое представление или null
     */
    @Override
    public String convertToDatabaseColumn(Money money) {
        if (money == null) {
            return null;
        }
        final String amount = money.amount().toPlainString();
        final String currency = currencyConverter.convertToDatabaseColumn(money.currency());
        return amount + "|" + currency;
    }

    /**
     * Восстанавливает объект {@link Money} из строки БД.
     * Ожидаемый формат: "сумма|валюта", где валюта — строка от {@link CurrencyConverter}.
     *
     * @param dbData строка из БД
     * @return объект Money
     * @throws NumberFormatException если сумма не является корректным числом
     */
    @Override
    public Money convertToEntityAttribute(String dbData) {
        if (dbData == null || !dbData.contains("|")) {
            return null;
        }
        final String[] parts = dbData.split("\\|", 2);
        final BigDecimal amount = new BigDecimal(parts[0]);
        final Currency currency = currencyConverter.convertToEntityAttribute(parts[1]);
        return new Money(amount, currency);
    }
}

