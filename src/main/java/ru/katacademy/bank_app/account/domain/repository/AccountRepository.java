package ru.katacademy.bank_app.account.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;

import java.util.Optional;

/**
 * Репозиторий для работы с аккаунтами
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Находит аккаунт по номеру счета.
     *
     * @param accountNumber номер счета аккаунта
     * @return Optional с найденным аккаунтом, если он есть
     */
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
}
