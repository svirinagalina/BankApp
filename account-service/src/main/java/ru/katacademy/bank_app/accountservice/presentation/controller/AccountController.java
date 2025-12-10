package ru.katacademy.bank_app.accountservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.bank_app.accountservice.adapters.web.response.AccountCreateRequest;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account controller", description = "API для управления банковскими счетами")
@Validated // Важно: для валидации параметров методов
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
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера"
            )
    })
    @PostMapping
    public ResponseEntity<AccountEntity> create(@Valid @RequestBody AccountCreateRequest request) {
        final AccountEntity accountEntity = accountService.createAccount(
                request.getUserId(),
                request.getAccountNumber(),
                request.getInitialBalance(),
                request.getCurrency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(accountEntity);
    }

    @Operation(summary = "Получить счет по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счет найден"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable @jakarta.validation.constraints.Min(1) Long id) {
        final AccountDto accountDto = accountService.getById(id);
        return ResponseEntity.ok(accountDto);
    }

    @Operation(summary = "Заблокировать счет")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Счет заблокирован"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable @jakarta.validation.constraints.Min(1) Long id) {
        accountService.blockAccountById(id);
        return ResponseEntity.noContent().build();
    }
}