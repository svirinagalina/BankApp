package ru.katacademy.bank_shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KycStatus {
    APPROVED("Одобрено"),
    REJECTED("Отклонено"),
    PENDING("На рассмотрении");

    private final String description;

    KycStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static KycStatus fromString(String value) {
        if (value == null) return null;

        for (KycStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown KycStatus: " + value);
    }

    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isPending() {
        return this == PENDING;
    }
}