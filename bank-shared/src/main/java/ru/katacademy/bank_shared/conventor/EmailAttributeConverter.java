package ru.katacademy.bank_shared.conventor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.Email;

/**
 * JPA-конвертер для value object'а {@link Email}.
 * Используется для автоматического преобразования Email в строку при сохранении в БД
 * и восстановления объекта Email из строки при загрузке.
 */
@Converter(autoApply = true)
public class EmailAttributeConverter implements AttributeConverter<Email, String> {

    /**
     * Вызывается при получении объекта {@link Email} в базу-данных и вернет String для записи в столбец
     * @param attribute {@link Email}
     * @return String
     */
    @Override
    public String convertToDatabaseColumn(Email attribute) {
        if (attribute == null) {
            return null;
        } else {
            return attribute.value();
        }
    }

    /**
     * Автоматически вызывается при получении значении из столбца в базе-данных и преобразует это значение в {@link Email}
     * @param dbData столбец из базы данных
     * @return Email
     */
    @Override
    public Email convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            return new Email(dbData);
        }
    }
}