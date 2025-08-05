package ru.katacademy.kycservice.application.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.domain.service.KycRequestService;
import ru.katacademy.kycservice.domain.service.MinioService;

import java.time.LocalDateTime;

@Service
public class KycRequestServiceImpl implements KycRequestService {
    private final KycRequestRepository kycRequestRepository;
    private final MinioService minioService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public KycRequestServiceImpl(KycRequestRepository kycRequestRepository, MinioService minioService) {
        this.kycRequestRepository = kycRequestRepository;
        this.minioService = minioService;
    }

    @Override
    public KycRequest createKycRequest(Long userId, String documentType, MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Invalid file size or empty file");
        }
        String fileKey = minioService.uploadFile(file);
        KycRequest kycRequest = new KycRequest(userId, documentType, fileKey, LocalDateTime.now());
        return kycRequestRepository.save(kycRequest);
    }
}
