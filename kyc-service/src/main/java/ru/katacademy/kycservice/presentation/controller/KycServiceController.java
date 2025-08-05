package ru.katacademy.kycservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.dto.KycRequestDTO;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.domain.mapper.KycRequestMapper;
import ru.katacademy.kycservice.domain.service.KycRequestService;

@RestController
@RequestMapping("/kyc")
public class KycServiceController {

    private final KycRequestService kycRequestService;
    private final KycRequestMapper kycRequestMapper;

    public KycServiceController(KycRequestService kycRequestService, KycRequestMapper kycRequestMapper) {
        this.kycRequestService = kycRequestService;
        this.kycRequestMapper = kycRequestMapper;
    }

    @PostMapping("/verify")
    public ResponseEntity<KycRequestDTO> verify(@RequestParam Long userId,
                                                @RequestParam String documentType,
                                                @RequestParam MultipartFile file) {
        KycRequest kycRequest = kycRequestService.createKycRequest(userId, documentType, file);
        KycRequestDTO dto = kycRequestMapper.toDTO(kycRequest);
        return ResponseEntity.ok(dto);
    }
}
