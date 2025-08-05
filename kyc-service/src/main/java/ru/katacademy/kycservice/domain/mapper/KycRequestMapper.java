package ru.katacademy.kycservice.domain.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.kycservice.application.dto.KycRequestDTO;
import ru.katacademy.kycservice.domain.entity.KycRequest;

@Component
public class KycRequestMapper {
    public KycRequestDTO toDTO(KycRequest kycRequest) {
        return new KycRequestDTO(
                kycRequest.getId(),
                kycRequest.getStatus());
    }
}
