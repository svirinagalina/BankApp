package ru.katacademy.kycservice.domain.service;

import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.domain.entity.KycRequest;

public interface KycRequestService {
    KycRequest start(Long userId);
    KycRequest getByUserId(Long userId);
    void uploadDocument(Long userId, String type, MultipartFile file);
}
