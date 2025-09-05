package ru.katacademy.kycservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Исключение, сигнализирующее об отсутствии KYC-заявки для указанного пользователя
 * - заявка не найдена
 * - попытка загрузить документ к несуществующей заявке
 * - репозиторий вернул Optional.empty() при поиске по userId
 * Автор: Белявский Г.А.
 * Дата: 01.09.2025
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class KycNotFoundException extends RuntimeException {
    public KycNotFoundException(Long userId) {
        super("KYC request not found for userId=" + userId);
    }
    public KycNotFoundException(UUID requestId) {
        super("KYC request not found for id=" + requestId);
    }
}
