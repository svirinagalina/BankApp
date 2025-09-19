package ru.katacademy.kycservice.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;
import ru.katacademy.kycservice.application.port.out.KycDocumentRepository;
import ru.katacademy.kycservice.application.port.out.KycEventPublisher;
import ru.katacademy.kycservice.application.port.out.KycEventAuditRepository;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycDocument;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.domain.service.KycRequestService;
import ru.katacademy.kycservice.application.port.out.MinioStorage;
import ru.katacademy.kycservice.exception.InvalidDocumentException;
import ru.katacademy.kycservice.exception.KycAlreadyExistsException;
import ru.katacademy.kycservice.exception.KycNotFoundException;
import java.time.Instant;

import java.util.Set;

/**
 * Реализация сервиса для обработки заявок KYC, включая загрузку документов и сохранение заявки.
 * <p>
 * Поля:
 * - kycRequestRepository: репозиторий для сохранения заявок KYC
 * - kycDocumentRepository: репозиторий документов KYC
 * - minioService: сервис для загрузки файлов в хранилище MinIO
 * - kycEventPublisher: паблишер событий смены KYC-статуса в Kafka
 * - kycEventAuditRepository: репозиторий аудита KYC-событий
 * - kycSource: значение поля source для публикуемых событий
 * - MAX_FILE_SIZE: максимальный допустимый размер файла (5 МБ)
 * - ALLOWED: допустимые MIME-типы (image/jpeg, image/png, application/pdf)
 * <p>
 * Методы:
 * - start(Long userId, String documentType, MultipartFile file):
 *     проверяет валидность файла, загружает его в MinIO и сохраняет заявку KYC с указанием пути к файлу,
 *     публикует событие о текущем статусе
 * - getByUserId(userId): возвращает заявку по userId или бросает 404
 * - uploadDocument(userId, type, file): валидирует файл, грузит в MinIO, создаёт запись документа и привязывает к заявке,
 *     при ошибке сохранения документа выполняет компенсацию (удаляет загруженный файл).
 * - changeStatusByUserId(Long userId, KycStatus newStatus, String source): Меняет KYC-статус заявки пользователя.
 *      Если статус не изменился, ничего не делает
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Service
public class KycRequestServiceImpl implements KycRequestService {
    private final KycRequestRepository kycRequestRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final MinioStorage minioStorage;
    private final KycEventPublisher kycEventPublisher;
    private final KycEventAuditRepository kycEventAuditRepository;
    private final String kycSource;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png","application/pdf");

    public KycRequestServiceImpl(KycRequestRepository repo,
                                 KycDocumentRepository kycDocumentRepository,
                                 MinioStorage minioStorage,
                                 KycEventPublisher kycEventPublisher,
                                 KycEventAuditRepository kycEventStore,
                                 @Value("${kyc.source}") String kycSource) {
        this.kycRequestRepository = repo;
        this.kycDocumentRepository = kycDocumentRepository;
        this.minioStorage = minioStorage;
        this.kycEventPublisher = kycEventPublisher;
        this.kycEventAuditRepository = kycEventStore;
        this.kycSource = kycSource;
    }

    @Transactional
    @Override
    public KycRequest start(Long userId) {
        if (kycRequestRepository.findByUserId(userId).isPresent()) {
            throw new KycAlreadyExistsException(userId);
        }
        KycRequest req = new KycRequest();
        req.setUserId(userId);
        var saved = kycRequestRepository.save(req);

        kycEventAuditRepository.save(saved.getId(), saved.getStatus());

        kycEventPublisher.publish(new KycStatusChangedEvent(String.valueOf(saved.getUserId()),
                                                                            saved.getStatus(),
                                                                            Instant.now(),
                                                                            kycSource));
        return saved;
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

        String fileKey = null;
        try {
            fileKey = minioStorage.uploadFile(file);

            var doc = new KycDocument();
            doc.setKycRequestId(request.getId());
            doc.setType(type);
            doc.setFileKey(fileKey);
            kycDocumentRepository.save(doc);

        } catch (RuntimeException ex) {
            if (fileKey != null) {
                minioStorage.deleteFile(fileKey);
            }
            throw ex;
        }
    }

    @Override
    @Transactional
    public KycRequest changeStatusByUserId(Long userId, KycStatus newStatus, String source) {
        var req = kycRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new KycNotFoundException(userId));

        var old = req.getStatus();
        if (old == newStatus) {
            return req;
        }

        req.setStatus(newStatus);
        var saved = kycRequestRepository.save(req);

        kycEventAuditRepository.save(saved.getId(), newStatus);
        kycEventPublisher.publish(new KycStatusChangedEvent(
                String.valueOf(saved.getUserId()),
                newStatus,
                Instant.now(),
                kycSource
        ));
        return saved;
    }
}
