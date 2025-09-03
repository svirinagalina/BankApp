package ru.katacademy.kycservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, сигнализирующее о попытке создать дубликат KYC-заявки для одного и того же пользователя
 * - повторный вызов POST /kyc/start?userId=..., когда запись с таким userId уже существует
 * - нарушение уникальности на уровне БД/репозитория
 * Автор: Белявский Г.А.
 * Дата: 01.09.2025
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class KycAlreadyExistsException extends RuntimeException {
    public KycAlreadyExistsException(Long userId) {
        super("KYC request already exists for userId=" + userId);
    }
}