package ru.katacademy.bank_app.account.domain.repository;

import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;

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
    Optional<AccountEntity> findByAccountNumber(AccountNumber accountNumber);

    /**
     * Сохраняет аккаунт в репозитории.
     * <p>
     * Если аккаунт уже существует, обновляет его данные.
     * Если аккаунт новый - добавляет его в репозиторий.
     * </p>
     *
     * @param accountEntity объект аккаунта для сохранения
     */
    AccountEntity save(AccountEntity accountEntity);
}
