package ru.katacademy.bank_app.account.infrastructure.repository;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.domain.repository.AccountRepository;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;

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

    /**
     * Конструктор репозитория.
     *
     * @param jpaAccountRepository JPA-репозиторий счетов (не должен быть null)
     * @throws IllegalArgumentException если jpaAccountRepository == null
     */
    public AccountRepositoryImpl(JpaAccountRepository jpaAccountRepository) {
        if (jpaAccountRepository == null) {
            throw new IllegalArgumentException("JpaAccountRepository не может быть null");
        }
        this.jpaAccountRepository = jpaAccountRepository;
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
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("AccountNumber не может быть null");
        }
        return jpaAccountRepository.findByAccountNumber(accountNumber.value());
    }

    /**
     * Сохраняет или обновляет счет.
     * <p>
     * Делегирует операцию сохранения JPA-репозиторию.
     * </p>
     *
     * @param account сохраняемый счет (не должен быть null)
     * @throws IllegalArgumentException если account == null
     */
    @Override
    public void save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account не может быть null");
        }
        jpaAccountRepository.save(account);
    }
}