package ru.katacademy.bank_app.shared.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.Currency;

@Converter(autoApply = false)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    // Пример хранения: "RUB:Russian Ruble:2"
    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.code() + ":" + attribute.name() + ":" + attribute.scale();
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        final String[] parts = dbData.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Невалидный формат валюты в базе данных");
        }
        return new Currency(parts[0], parts[1], Integer.parseInt(parts[2]));
    }
}
