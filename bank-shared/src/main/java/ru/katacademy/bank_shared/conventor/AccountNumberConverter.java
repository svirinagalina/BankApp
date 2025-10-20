package ru.katacademy.bank_shared.conventor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.katacademy.bank_shared.valueobject.AccountNumber;

@Converter(autoApply = false)
public class AccountNumberConverter implements AttributeConverter<AccountNumber, String> {

    @Override
    public String convertToDatabaseColumn(AccountNumber accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accountNumber.accountNumber();
    }

    @Override
    public AccountNumber convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return new AccountNumber(dbData);
    }
}
