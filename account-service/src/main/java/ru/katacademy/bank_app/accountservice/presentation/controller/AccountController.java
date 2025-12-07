package ru.katacademy.bank_app.accountservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.katacademy.bank_app.accountservice.adapters.web.response.AccountDtoRequest;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;

import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_shared.exception.MaxAccountsExceededException;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account controller", description = "API для управления банковскими счетами")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Создание нового счета",
            description = "Создает новый банковский счет для указанного пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Счет успешно создан",
                    content = @Content(schema = @Schema(implementation = AccountEntity.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера"
            )
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AccountDtoRequest req) {
        try {
            final AccountEntity accountEntity = accountService.createAccount(req.getUser(), req.getNumber(), req.getInitialBalance());
            return ResponseEntity.status(HttpStatus.CREATED).body(accountEntity);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (MaxAccountsExceededException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Получить счет по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счет найден"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable Long id) {

        final AccountDto accountDto = accountService.getById(id);
        return ResponseEntity.ok(accountDto);
    }

    @Operation(summary = "Заблокировать счет")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Счет заблокирован"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        accountService.blockAccountById(id);
        return ResponseEntity.noContent().build();
    }
}
