package ru.katacademy.kycservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.domain.service.KycRequestService;
import ru.katacademy.kycservice.application.port.out.MinioStorage;
import ru.katacademy.kycservice.exception.InvalidDocumentException;
import ru.katacademy.kycservice.exception.KycAlreadyExistsException;
import ru.katacademy.kycservice.exception.KycNotFoundException;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycDocumentEntity;
import ru.katacademy.kycservice.infrastructure.repository.KycDocumentJpaRepository;
import ru.katacademy.kycservice.infrastructure.repository.KycRequestJpaRepository;

import java.util.Set;

/**
 * Реализация сервиса для обработки заявок KYC, включая загрузку документов и сохранение заявки.
 * <p>
 * Поля:
 * - kycRequestRepository: репозиторий для сохранения заявок KYC
 * - kycRequestJpaRepository: JPA-репозиторий сущности заявки (нужен для связи документа по FK);
 * - kycDocumentJpaRepository: JPA-репозиторий документов KYC;
 * - minioService: сервис для загрузки файлов в хранилище MinIO
 * - MAX_FILE_SIZE: максимальный допустимый размер файла (5 МБ)
 * - ALLOWED: допустимые MIME-типы (image/jpeg, image/png, application/pdf)
 * <p>
 * Методы:
 * - start(Long userId, String documentType, MultipartFile file):
 *     проверяет валидность файла, загружает его в MinIO и сохраняет заявку KYC с указанием пути к файлу
 * - getByUserId(userId): возвращает заявку по userId или бросает 404
 * - uploadDocument(userId, type, file): валидирует файл, грузит в MinIO, создаёт запись документа и привязывает к заявке,
 *     при ошибке сохранения документа выполняет компенсацию (удаляет загруженный файл).
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Service
public class KycRequestServiceImpl implements KycRequestService {
    private final KycRequestRepository kycRequestRepository;
    private final KycRequestJpaRepository kycRequestJpaRepository;
    private final KycDocumentJpaRepository kycDocumentJpaRepository;
    private final MinioStorage minioStorage;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","application/pdf");

    public KycRequestServiceImpl(KycRequestRepository repo,
                                 KycRequestJpaRepository jpaReq,
                                 KycDocumentJpaRepository docRepo,
                                 MinioStorage minioStorage) {
        this.kycRequestRepository = repo;
        this.kycRequestJpaRepository = jpaReq;
        this.kycDocumentJpaRepository = docRepo;
        this.minioStorage = minioStorage;
    }

    @Transactional
    @Override
    public KycRequest start(Long userId) {
        if (kycRequestRepository.findByUserId(userId).isPresent()) {
            throw new KycAlreadyExistsException(userId);
        }
        KycRequest req = new KycRequest();
        req.setUserId(userId);
        return kycRequestRepository.save(req);
    }

    @Transactional(readOnly = true)
    @Override
    public KycRequest getByUserId(Long userId) {
        return kycRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new KycNotFoundException(userId));
    }

    @Transactional
    @Override
    public void uploadDocument(Long userId, String type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDocumentException("Empty file");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidDocumentException("File too large (max 5MB)");
        }
        var contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new InvalidDocumentException("Unsupported content type: " + contentType);
        }

        var request = kycRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new KycNotFoundException(userId));

        var requestEntity = kycRequestJpaRepository.findById(request.getId())
                .orElseThrow(() -> new KycNotFoundException(userId));

        String fileKey = null;
        try {
            fileKey = minioStorage.uploadFile(file);

            var doc = new KycDocumentEntity();
            doc.setKycRequest(requestEntity);
            doc.setType(type);
            doc.setFileKey(fileKey);
            kycDocumentJpaRepository.save(doc);

        } catch (RuntimeException ex) {
            if (fileKey != null) {
                minioStorage.deleteFile(fileKey);
            }
            throw ex;
        }
    }
}
