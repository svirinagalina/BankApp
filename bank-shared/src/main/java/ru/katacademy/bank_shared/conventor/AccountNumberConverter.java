package ru.katacademy.bank_shared.conventor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.AccountNumber;

/**
 * JPA-конвертер для value object'а {@link AccountNumber}.
 * Используется для автоматического преобразования AccountNumber в строку при сохранении в БД
 * и восстановления объекта AccountNumber из строки при загрузке.
 */
@Converter(autoApply = false)
public class AccountNumberConverter implements AttributeConverter<AccountNumber, String> {

    /**
     * Преобразует объект {@link AccountNumber} в строку для хранения в БД.
     * @return строковое представление или null
     */
    @Override
    public String convertToDatabaseColumn(AccountNumber accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accountNumber.accountNumber();
    }

    /**
     * Восстанавливает объект {@link AccountNumber} из строки БД.
     * @param dbData строка из БД
     * @return объект AccountNumber
     */
    @Override
    public AccountNumber convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return new AccountNumber(dbData);
    }
}
