package ru.katacademy.kycservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, сигнализирующее о невалидном загружаемом документе
 * - файл пустой или отсутствует
 * - размер файла превышает допустимый предел (по умолчанию 5 МБ)
 * - неподдерживаемый MIME-тип (например, не входит в список: image/jpeg, image/png, application/pdf)
 * Автор: Белявский Г.А.
 * Дата: 01.09.2025
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException(String msg) { super(msg); }
}