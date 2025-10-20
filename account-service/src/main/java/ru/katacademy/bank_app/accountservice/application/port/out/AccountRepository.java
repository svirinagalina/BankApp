package ru.katacademy.bank_app.accountservice.application.port.out;

import ru.katacademy.bank_app.accountservice.domain.entity.Account;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findById(Long id);

    Account save(Account account);
}
