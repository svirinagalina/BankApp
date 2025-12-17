package ru.katacademy.bank_app.accountservice.adapters.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
import ru.katacademy.bank_app.accountservice.adapters.web.util.WebLayerParser;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.domain.service.AccountService;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
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
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
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
                    description = "Ошибка валидации входных данных"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Превышено максимальное количество счетов"
            )
    })
    @PostMapping
    public ResponseEntity<AccountCreatedResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        try {
            final Long userId = WebLayerParser.parseLongId(request.getUserId());
            final UserDto userDto = userService.getById(userId);
            final AccountNumber accountNumber = WebLayerParser.parseAccountNumber(request.getAccountNumber());
            final Money initialBalance = WebLayerParser.parseMoney(request.getInitialBalance(), request.getCurrency());

            final AccountCreatedResponse response = AccountCreatedResponse.builder()
                    .accountId(null) // временно
                    .accountNumber(request.getAccountNumber())
                    .userId(userId)
                    .initialBalance(request.getInitialBalance())
                    .currency(request.getCurrency())
                    .createdAt(LocalDateTime.now())
                    .message("Счет успешно создан")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    AccountCreatedResponse.builder()
                            .message("Ошибка: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AccountCreatedResponse.builder()
                            .message("Внутренняя ошибка сервера")
                            .build()
            );
        }
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
                    description = "Счет не найден"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        final AccountDto accountDto = accountService.getById(id);
        final AccountResponse response = AccountWebMapper.toAccountResponse(accountDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Заблокировать счет")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Счет заблокирован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Счет не найден"
            )
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        accountService.blockAccountById(id);
        return ResponseEntity.noContent().build();
    }
}