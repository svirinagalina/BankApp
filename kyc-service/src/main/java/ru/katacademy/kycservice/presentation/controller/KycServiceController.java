package ru.katacademy.kycservice.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.dto.KycRequestDTO;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.domain.mapper.KycRequestMapper;
import ru.katacademy.kycservice.domain.service.KycRequestService;

/**
 * REST-контроллер для обработки запросов на верификацию KYC.
 * <p>
 * Поля:
 * - kycRequestService: сервис для работы с заявками KYC
 * - kycRequestMapper: маппер для преобразования между сущностями и DTO
 * <p>
 * Методы:
 * - start: инициация KYC по userId, создаёт заявку со статусом PENDING
 * - get: получение текущего статуса заявки и времени последнего обновления
 * - upload: загрузка документа и привязка к заявке
 * <p>
 *     Ошибки:
 * - 409 CONFLICT — заявка уже существует
 * - 404 NOT FOUND — заявки для указанного userId не найдено
 * - 400 BAD REQUEST — невалидный документ (пустой/слишком большой/неподдерживаемый тип)
 * </p>
 *
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@RestController
@RequestMapping("/kyc")
public class KycServiceController {

    private final KycRequestService kycRequestService;
    private final KycRequestMapper kycRequestMapper;

    public KycServiceController(KycRequestService kycRequestService, KycRequestMapper kycRequestMapper) {
        this.kycRequestService = kycRequestService;
        this.kycRequestMapper = kycRequestMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<KycRequestDTO> createKycRequest(@RequestParam Long userId) {
        KycRequest req = kycRequestService.start(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(kycRequestMapper.toDTO(req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<KycRequestDTO> getKycStatus(@PathVariable Long userId) {
        KycRequest req = kycRequestService.getByUserId(userId);
        return ResponseEntity.ok(kycRequestMapper.toDTO(req));
    }

    @PostMapping(path = "/{userId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadKycDocument(@PathVariable Long userId,
                                                  @RequestParam String type,
                                                  @RequestParam MultipartFile file) {
        kycRequestService.uploadDocument(userId, type, file);
        return ResponseEntity.accepted().build();
    }
}
