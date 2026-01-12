package ru.katacademy.bank_app.accountservice.adapters.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.bank_app.accountservice.adapters.web.mapper.AccountWebMapper;
import ru.katacademy.bank_app.accountservice.adapters.web.request.account.CreateAccountRequest;
import ru.katacademy.bank_app.accountservice.adapters.web.response.account.AccountCreatedResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.response.account.AccountResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.response.error.ErrorResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.util.WebLayerParser;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.exception.AccountNotFoundException;
import ru.katacademy.bank_shared.exception.MaxAccountsExceededException;
import ru.katacademy.bank_shared.exception.UserNotFoundException;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account controller", description = "API для управления банковскими счетами")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @Operation(
            summary = "Создание нового счета",
            description = "Создает новый банковский счет для указанного пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateAccountRequest.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = """
                                            {
                                              "userId": "123",
                                              "accountNumber": "40817810099910004321",
                                              "initialBalance": 1000.00,
                                              "currency": "RUB",
                                              "accountType": "CHECKING"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Счет успешно создан",
                    content = @Content(schema = @Schema(implementation = AccountCreatedResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Ошибка валидации",
                                    value = """
                                            {
                                              "timestamp": "2026-01-10 10:37:07",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Ошибка валидации входных данных",
                                              "path": "/api/accounts",
                                              "errors": [
                                                "Номер счета должен содержать 20 символов",
                                                "Валюта должна содержать 3 символа"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Пользователь не найден",
                                    value = """
                                            {
                                              "timestamp": "2026-01-10 10:37:07",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Пользователь с id 999 не найден",
                                              "path": "/api/accounts"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: номер счета уже существует или превышен лимит",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Дубликат номера счета",
                                            value = """
                                                    {
                                                      "timestamp": "2026-01-10 10:37:07",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Номер счета уже существует: 40817810099910009999",
                                                      "path": "/api/accounts"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Превышен лимит счетов",
                                            value = """
                                                    {
                                                      "timestamp": "2026-01-10 10:37:07",
                                                      "status": 409,
                                                      "error": "Conflict",
                                                      "message": "Превышено максимальное количество счетов (5)",
                                                      "path": "/api/accounts"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        try {
            return handleCreateAccountRequest(request);
        } catch (UserNotFoundException e) {
            return buildUserNotFoundErrorResponse(e);
        } catch (MaxAccountsExceededException e) {
            return buildMaxAccountsExceededErrorResponse(e);
        } catch (DataIntegrityViolationException e) {
            return buildAccountNumberConflictErrorResponse(request.getAccountNumber());
        } catch (IllegalArgumentException e) {
            return buildBadRequestErrorResponse(e);
        } catch (Exception e) {
            return buildInternalServerErrorResponse(e);
        }
    }

    private ResponseEntity<?> handleCreateAccountRequest(CreateAccountRequest request) {
        final Long userId = WebLayerParser.parseLongId(request.getUserId());
        final UserEntity userEntity = userService.getEntityById(userId);
        final AccountNumber accountNumber = WebLayerParser.parseAccountNumber(request.getAccountNumber());
        final Money initialBalance = WebLayerParser.parseMoney(request.getInitialBalance(), request.getCurrency());

        final AccountEntity createdAccount = accountService.createAccount(
                userEntity,
                accountNumber,
                initialBalance
        );

        final AccountCreatedResponse response = buildAccountCreatedResponse(createdAccount, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private AccountCreatedResponse buildAccountCreatedResponse(
            AccountEntity createdAccount,
            Long userId,
            CreateAccountRequest request) {
        return AccountCreatedResponse.builder()
                .accountId(createdAccount.getId())
                .accountNumber(request.getAccountNumber())
                .userId(userId)
                .initialBalance(request.getInitialBalance())
                .currency(request.getCurrency())
                .createdAt(LocalDateTime.now())
                .message("Счет успешно создан")
                .build();
    }

    private ResponseEntity<ErrorResponse> buildUserNotFoundErrorResponse(UserNotFoundException e) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Пользователь не найден: " + e.getMessage())
                .path("/api/accounts")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private ResponseEntity<ErrorResponse> buildMaxAccountsExceededErrorResponse(MaxAccountsExceededException e) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(e.getMessage())
                .path("/api/accounts")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    private ResponseEntity<ErrorResponse> buildAccountNumberConflictErrorResponse(String accountNumber) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message("Номер счета уже существует: " + accountNumber)
                .path("/api/accounts")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    private ResponseEntity<ErrorResponse> buildBadRequestErrorResponse(IllegalArgumentException e) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(e.getMessage())
                .path("/api/accounts")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<ErrorResponse> buildInternalServerErrorResponse(Exception e) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Внутренняя ошибка сервера: " + e.getMessage())
                .path("/api/accounts")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Operation(summary = "Получить счет по ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Счет найден",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Счет не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Счет не найден",
                                    value = """
                                            {
                                              "timestamp": "2026-01-10 10:37:07",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Аккаунт с id 999 не найден",
                                              "path": "/api/accounts/999"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(
            @PathVariable
            @Parameter(description = "ID счета", example = "7")
            @Schema(description = "ID счета",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    example = "7")
            Long id) {
        try {
            final AccountDto accountDto = accountService.getById(id);
            final AccountResponse response = AccountWebMapper.toAccountResponse(accountDto);
            return ResponseEntity.ok(response);
        } catch (AccountNotFoundException e) {
            final ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Not Found")
                    .message(e.getMessage())
                    .path("/api/accounts/" + id)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Заблокировать счет")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Счет заблокирован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Счет не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Счет не найден",
                                    value = """
                                            {
                                              "timestamp": "2026-01-10 10:37:07",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Аккаунт с id 999 не найден",
                                              path: "/api/accounts/999/block"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockAccount(
            @PathVariable
            @Parameter(description = "ID счета для блокировки", example = "7")
            @Schema(description = "ID счета для блокировки",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    example = "7")
            Long id) {
        try {
            accountService.blockAccountById(id);
            return ResponseEntity.noContent().build();
        } catch (AccountNotFoundException e) {
            final ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Not Found")
                    .message(e.getMessage())
                    .path("/api/accounts/" + id + "/block")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}