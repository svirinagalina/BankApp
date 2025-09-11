package ru.katacademy.bank_app.accountservice.application.dto;

import ru.katacademy.bank_shared.enums.KycStatus;

public record KycRequestDTO(
        Long id,
        KycStatus status
) {

}
