package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_app.accountservice.domain.entity.Account;

import java.util.Optional;

/**
 * Порт выхода для работы с сущностями аккаунтов.
 * CRUD‑операции над {@link Account}.
 */
public interface AccountRepository {

    /**
     * Ищет аккаунт по идентификатору.
     *
     * @param id уникальный идентификатор аккаунта
     * @return Optional с найденным аккаунтом или пустой Optional, если не найден
     */
    Optional<Account> findById(Long id);

    /**
     * Сохраняет новый аккаунт или обновляет существующий.
     *
     * @param account объект {@link Account} для сохранения
     * @return сохранённый аккаунт с заполненным полем {@code id}
     */
    Account save(Account account);
}
