package ru.katacademy.bank_app.accountservice.application.port.out;

import org.springframework.stereotype.Repository;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper.AccountMapper;
import ru.katacademy.bank_app.accountservice.infrastructure.repository.AccountJpaRepository;

import java.util.Optional;

/**
 * Реализация порта {@link AccountRepository}.
 * Делегирует операции JPA‑репозиторию и маппит сущности ↔ домен.
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository  accountJpaRepository;

    public AccountRepositoryImpl(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountJpaRepository.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        final AccountEntity  accountEntity = AccountMapper.toEntity(account);
        final AccountEntity savedAccountEntity = accountJpaRepository.save(accountEntity);
        return AccountMapper.toDomain(savedAccountEntity);

    }
}
