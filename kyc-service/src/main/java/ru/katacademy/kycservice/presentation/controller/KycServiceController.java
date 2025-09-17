package ru.katacademy.kycservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Инициация KYC процесса", description = "Создает новую заявку KYC для указанного пользователя.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка успешно создана",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = KycRequestDTO.class))),
            @ApiResponse(responseCode = "409", description = "Заявка уже существует", content = @Content)
    })
    @PostMapping("/start")
    public ResponseEntity<KycRequestDTO> createKycRequest(@RequestParam Long userId) {
        KycRequest req = kycRequestService.start(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(kycRequestMapper.toDTO(req));
    }

    @Operation(summary = "Получение статуса KYC", description = "Возвращает текущий статус заявки KYC для пользователя.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус KYC найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = KycRequestDTO.class))),
            @ApiResponse(responseCode = "404", description = "Заявка KYC не найдена", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<KycRequestDTO> getKycStatus(@PathVariable Long userId) {
        KycRequest req = kycRequestService.getByUserId(userId);
        return ResponseEntity.ok(kycRequestMapper.toDTO(req));
    }

    @Operation(summary = "Загрузка документа для KYC", description = "Позволяет загрузить документ для указанной заявки KYC.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Документ успешно загружен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректный документ", content = @Content),
            @ApiResponse(responseCode = "404", description = "Заявка KYC не найдена", content = @Content)
    })
    @PostMapping(path = "/{userId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadKycDocument(
            @Parameter(description = "ID пользователя для загрузки документа", example = "123") @PathVariable Long userId,
            @Parameter(description = "Тип документа (passport, utility_bill и т.д.)", example = "passport") @RequestParam String type,
            @Parameter(description = "Файл документа для загрузки") @RequestParam MultipartFile file) {
        kycRequestService.uploadDocument(userId, type, file);
        return ResponseEntity.accepted().build();
    }
}
