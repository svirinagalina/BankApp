package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;

import java.util.Optional;

/**
 * JPA-репозиторий для работы с банковскими счетами.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository}
 * и кастомные методы для поиска счетов. Реализация методов генерируется
 * автоматически Spring Data JPA на основе соглашений об именовании.
 * </p>
 *
 * @author Sheffy
 * @see JpaRepository
 * @see Account
 */
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Находит счет по номеру.
     * <p>
     * Генерирует JPQL-запрос вида: {@code SELECT a FROM Account a WHERE a.accountNumber = ?1}
     * </p>
     *
     * @param accountNumber номер счета (в формате строки)
     * @return {@link Optional} с найденным счетом или {@link Optional#empty()}, если счет не существует
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}