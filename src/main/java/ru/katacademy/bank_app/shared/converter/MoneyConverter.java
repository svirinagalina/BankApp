package ru.katacademy.bank_app.shared.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_app.shared.valueobject.Currency;
import ru.katacademy.bank_app.shared.valueobject.Money;

import java.math.BigDecimal;

@Converter
public class MoneyConverter implements AttributeConverter<Money, String> {

    private final CurrencyConverter currencyConverter = new CurrencyConverter();

    @Override
    public String convertToDatabaseColumn(Money money) {
        if (money == null) {
            return null;
        }
        final String amount = money.amount().toPlainString();
        final String currency = currencyConverter.convertToDatabaseColumn(money.currency());
        return amount + "|" + currency;
    }

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

