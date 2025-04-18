package ru.katacademy.bank_app.account.domain.repository;

import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;

import java.util.Optional;

/**
 * Репозиторий для работы с аккаунтами
 */
public interface AccountRepository {

    /**
     * Находит аккаунт по номеру счета.
     *
     * @param accountNumber номер счета аккаунта
     * @return Optional с найденным аккаунтом, если он есть
     */
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);

    /**
     * Сохраняет аккаунт в репозитории.
     * <p>
     * Если аккаунт уже существует, обновляет его данные.
     * Если аккаунт новый - добавляет его в репозиторий.
     * </p>
     *
     * @param account объект аккаунта для сохранения
     */
    void save(Account account);
}
