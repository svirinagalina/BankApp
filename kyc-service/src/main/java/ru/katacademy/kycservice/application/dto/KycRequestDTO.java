package ru.katacademy.kycservice.application.dto;


import ru.katacademy.kycservice.domain.enumtype.KycStatus;

public record KycRequestDTO(
        Long id,
        KycStatus status
) {
}
