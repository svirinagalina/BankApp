package ru.katacademy.bank_app.audit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.bank_app.audit.application.service.AuditService;
import ru.katacademy.bank_app.account.domain.entity.AuditEntry;

import java.util.List;

/**
 * Контроллер для работы с аудит-логами.
 * <p>
 * Предоставляет API для получения записей аудита.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/audit")
public class AuditController {
    private final AuditService auditService;

    /**
     * Получает все записи аудита.
     *
     * @return список всех аудит-записей
     */
    @GetMapping
    public List<AuditEntry> getAllAudits() {
        return auditService.getAllAudits();
    }

    /**
     * Получает записи аудита по типу события.
     *
     * @param eventType тип события (например, "USER_REGISTER")
     * @return список аудит-записей указанного типа
     */
    @GetMapping("/type/{eventType}")
    public List<AuditEntry> getAuditsByType(@PathVariable String eventType) {
        return auditService.getAllAuditsByType(eventType);
    }
}
