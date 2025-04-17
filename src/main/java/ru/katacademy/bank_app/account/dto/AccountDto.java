package ru.katacademy.bank_app.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountDto {

    private BigDecimal amount;
    private String currency;
    private String status;
    private String accountNumber;

}

