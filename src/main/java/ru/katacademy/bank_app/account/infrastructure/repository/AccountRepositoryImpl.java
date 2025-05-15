package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;

import java.util.Optional;

/**
 * Реализация репозитория счетов на основе Spring Data JPA.
 * <p>
 * Адаптирует JPA-специфичный репозиторий ({@link JpaAccountRepository})
 * к доменному интерфейсу {@link AccountRepository}.
 * </p>
 *
 * @author Sheffy
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    public AccountRepositoryImpl(JpaAccountRepository jpaAccountRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }

    public static AccountRepositoryImpl create(JpaAccountRepository jpaAccountRepository) {
        if (jpaAccountRepository == null) {
            throw new IllegalArgumentException("JpaAccountRepository не может быть null");
        }
        return new AccountRepositoryImpl(jpaAccountRepository);
    }

    /**
     * Находит счет по номеру.
     * <p>
     * Преобразует доменный объект {@link AccountNumber} в строку
     * и делегирует вызов JPA-репозиторию.
     * </p>
     *
     * @param accountNumber номер счета (не должен быть null)
     * @return {@link Optional} с найденным счетом или пустой {@link Optional}, если счет не найден
     * @throws IllegalArgumentException если accountNumber == null
     */
    @Override
    public Optional<AccountEntity> findByAccountNumber(AccountNumber accountNumber) {
        return jpaAccountRepository.findByAccountNumber(accountNumber.value());
    }


    /**
     * Сохраняет или обновляет банковский счет в репозитории.
     * <p>
     * Преобразует доменную модель в JPA-сущность, сохраняет её и возвращает
     * обновленную доменную модель с актуальными данными.
     * </p>
     *
     * @param accountEntity доменная модель счета для сохранения (не null)
     * @return сохраненная доменная модель с актуальными данными
     * @throws IllegalArgumentException если переданный account == null
     * @throws DataAccessException при ошибках доступа к данным
     * @author Sheffy
     */
    @Override
    public AccountEntity save(AccountEntity accountEntity) {
        return jpaAccountRepository.save(accountEntity);
    }
}