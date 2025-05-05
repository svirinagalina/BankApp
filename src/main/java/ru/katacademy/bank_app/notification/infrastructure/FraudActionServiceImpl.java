package ru.katacademy.bank_app.notification.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.account.application.service.AccountService;
import ru.katacademy.bank_app.notification.application.FraudActionService;
import ru.katacademy.bank_shared.valueobject.AccountNumber;

/**
 * Класс отвечающает за выполнение действий при обнаружении мошенничества.
 * Вызывает блокировку аккаунта через {@link AccountService} при переданном
 * номере аккаунта.
 * <p>
 * Поля:
 * - accountService: сервис выполнения операций со счетами
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-04-30
 */
@Service
@RequiredArgsConstructor
public class FraudActionServiceImpl implements FraudActionService {

    private final AccountService accountService;

    /**
     * Блокирует аккаунт с указанным номером, делегируя вызов
     * {@link AccountService#blockAccountByNumber(AccountNumber)}.
     *
     * @param accountNumber номер аккаунта, который требуется заблокировать
     */
    @Override
    public void blockAccount(AccountNumber accountNumber) {
        accountService.blockAccountByNumber(accountNumber);
    }
}
