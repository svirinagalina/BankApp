package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.application.port.out.AccountRepository;
import ru.katacademy.bank_app.accountservice.application.port.out.TransferEventPublisher;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.accountservice.domain.mapper.AccountMapper;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_app.accountservice.infrastructure.repository.AccountJpaRepository;
import ru.katacademy.bank_shared.event.TransferCompletedEvent;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Тест лоя проверки работы методов AccountServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransferEventPublisher transferEventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    // Тестовые данные
    AccountNumber number = new AccountNumber("12345678901234567890");
    private final Currency rub = new Currency("RUB", "Russian Ruble", 2);
    private final Money money = new Money(BigDecimal.valueOf(50_000), rub);
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void createAccount_WhenValidCommandProvided() {
        final UserEntity user = mock(UserEntity.class);

        when(accountJpaRepository.countByUserId(anyLong())).thenReturn(0L);

        final AccountEntity savedEntity = new AccountEntity(number, user, money, AccountStatus.ACTIVE, now, now);
        when(accountJpaRepository.save(any(AccountEntity.class))).thenReturn(savedEntity);

        final AccountEntity result = accountService.createAccount(user, number, money);

        assertNotNull(result);
        verify(accountJpaRepository).countByUserId(anyLong());
        verify(accountJpaRepository).save(any(AccountEntity.class));
    }

    @Test
    void createAccount_maxAccountsExceeded() {
        final UserEntity user = mock(UserEntity.class);

        when(accountJpaRepository.countByUserId(anyLong())).thenReturn(5L);
        try {
            accountService.createAccount(user, number, money);
        } catch (RuntimeException e) {
        }
        verify(accountJpaRepository).countByUserId(anyLong());
        verify(accountJpaRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        final UserEntity user = mock(UserEntity.class);
        final Account account = new Account(1L, user, number, money, AccountStatus.ACTIVE, now, now);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        final AccountDto dto = mock(AccountDto.class);
        when(accountMapper.toDto(account)).thenReturn(dto);

        final AccountDto result = accountService.getById(1L);
        assertNotNull(result);
        assertSame(dto, result);

        verify(accountRepository).findById(1L);
        verify(accountMapper).toDto(account);
    }

    @Test
    void getById_notFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            accountService.getById(1L);
        } catch (RuntimeException e) {
        }
        verify(accountRepository).findById(1L);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void blockAccountById_success() {
        final UserEntity user = mock(UserEntity.class);
        final AccountEntity entity = new AccountEntity(number, user, money, AccountStatus.ACTIVE, now, now);

        when(accountJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(accountJpaRepository.save(entity)).thenReturn(entity);

        accountService.blockAccountById(1L);

        assertEquals(AccountStatus.BLOCKED, entity.getStatus());
        verify(accountJpaRepository).findById(1L);
        verify(accountJpaRepository).save(entity);
    }

    @Test
    void transferMoney_success() {
        final AccountNumber numFrom = new AccountNumber("12345678901234567890");
        final AccountNumber numTo = new AccountNumber("09876543210987654321");
        final UserEntity user = mock(UserEntity.class);

        final Money moneyFrom = new Money(BigDecimal.valueOf(50_000), rub);
        final Money moneyTo = new Money(BigDecimal.valueOf(80_000), rub);

        final Account accountFrom = new Account(1L, user, numFrom, moneyFrom, AccountStatus.ACTIVE, now, now);
        final Account accountTo = new Account(2L, user, numTo, moneyTo, AccountStatus.ACTIVE, now, now);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        final Money transferAmount = new Money(BigDecimal.valueOf(5_000), rub);
        accountService.transferMoney(1L, 2L, transferAmount);

        assertEquals(BigDecimal.valueOf(45_000), accountFrom.getBalance().amount());
        assertEquals(BigDecimal.valueOf(85_000), accountTo.getBalance().amount());

        verify(accountRepository, times(1)).save(accountFrom);
        verify(accountRepository, times(1)).save(accountTo);
        verify(transferEventPublisher, times(1)).publish(any(TransferCompletedEvent.class));
    }

    @Test
    void transferMoney_insufficientFunds() {
        final AccountNumber numFrom = new AccountNumber("12345678901234567890");
        final AccountNumber numTo = new AccountNumber("09876543210987654321");
        final UserEntity user = mock(UserEntity.class);

        final Money moneyFrom = new Money(BigDecimal.valueOf(50_000), rub);
        final Money moneyTo = new Money(BigDecimal.valueOf(80_000), rub);

        final Account accountFrom = new Account(1L, user, numFrom, moneyFrom, AccountStatus.ACTIVE, now, now);
        final Account accountTo = new Account(2L, user, numTo, moneyTo, AccountStatus.ACTIVE, now, now);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        final Money transferAmount = new Money(BigDecimal.valueOf(60_000), rub);

        try {
            accountService.transferMoney(1L, 2L, transferAmount);
        } catch (RuntimeException e) {
        }

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transferEventPublisher);
    }

    @Test
    void transferMoney_accountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            accountService.transferMoney(99L, 2L, new Money(BigDecimal.valueOf(5_000), rub));
        } catch (RuntimeException e) {
        }

        verify(accountRepository).findById(99L);
        verifyNoInteractions(transferEventPublisher);
    }

    @Test
    void transferMoney_accountNotActive() {
        final AccountNumber numFrom = new AccountNumber("12345678901234567890");
        final AccountNumber numTo = new AccountNumber("09876543210987654321");
        final UserEntity user = mock(UserEntity.class);

        final Money moneyFrom = new Money(BigDecimal.valueOf(50_000), rub);
        final Money moneyTo = new Money(BigDecimal.valueOf(80_000), rub);

        final Account accountFrom = new Account(1L, user, numFrom, moneyFrom, AccountStatus.BLOCKED, now, now);
        final Account accountTo = new Account(2L, user, numTo, moneyTo, AccountStatus.ACTIVE, now, now);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        try {
            accountService.transferMoney(1L, 2L, new Money(BigDecimal.valueOf(5_000), rub));
        } catch (RuntimeException e) {
        }

        verifyNoInteractions(transferEventPublisher);
        verify(accountRepository, never()).save(any());
    }
}
