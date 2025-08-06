package ru.katacademy.kycservice.domain.service;


import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.domain.entity.KycRequest;

public interface KycRequestService {
    KycRequest createKycRequest(Long userId, String documentType, MultipartFile file);
}
