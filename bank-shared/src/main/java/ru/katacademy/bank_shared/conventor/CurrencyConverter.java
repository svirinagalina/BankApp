package ru.katacademy.bank_shared.conventor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.Currency;

/**
 * JPA-конвертер для value object'а {@link Currency}.
 * Используется для автоматического преобразования Currency в строку при сохранении в БД
 * и восстановления объекта Currency из строки при загрузке.
 */
@Converter(autoApply = false)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    // Пример хранения: "RUB:Russian Ruble:2"
    /**
     * Преобразует объект {@link Currency} в строку формата "код:название:число" для хранения в БД.
     *
     * @param attribute объект Currency
     * @return строковое представление
     */
    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.code() + ":" + attribute.name() + ":" + attribute.scale();
    }

    /**
     * Преобразует строку из БД обратно в объект {@link Currency}.
     * Ожидаемый формат: "код:название:число".
     *
     * @param dbData строковое представление объекта
     * @return объект Currency
     * @throws IllegalArgumentException если строка имеет неверный формат или нечисловое значение в конце
     */
    @Override
    public Currency convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        final String[] parts = dbData.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Невалидный формат валюты в базе данных.");
        }
        try {
            return new Currency(parts[0], parts[1], Integer.parseInt(parts[2]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Невалидное значение валюты:" + parts[2], ex);
        }
    }
}
