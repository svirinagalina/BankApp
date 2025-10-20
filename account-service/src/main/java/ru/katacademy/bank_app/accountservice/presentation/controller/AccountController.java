package ru.katacademy.bank_app.accountservice.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.exception.MaxAccountsExceededException;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
        try {
            AccountEntity e = accountService.createAccount(req.getUser(), req.getNumber(), req.getInitialBalance());
            return ResponseEntity.status(HttpStatus.CREATED).body(e);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (MaxAccountsExceededException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) {

        AccountDto accountDto = accountService.getById(id);
        return ResponseEntity.ok(accountDto);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        accountService.blockAccountById(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateAccountRequest {

        private UserEntity user;
        private AccountNumber number;
        private Money initialBalance;

        public UserEntity getUser() {
            return user;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public AccountNumber getNumber() {
            return number;
        }

        public void setNumber(AccountNumber number) {
            this.number = number;
        }

        public Money getInitialBalance() {
            return initialBalance;
        }

        public void setInitialBalance(Money initialBalance) {
            this.initialBalance = initialBalance;
        }
    }
}
