package ru.katacademy.bank_app.accountservice.adapters.web.request.kyc;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на обновление KYC статуса")
public class UpdateKycStatusRequest {

    @NotBlank(message = "ID пользователя не может быть пустым")
    @Pattern(regexp = "^\\\\d+$", message = "ID пользователя должен быть числом")
    @Schema(description = "ID пользователя",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId; // ✅ String - будет парситься в Long

    @NotBlank(message = "Статус KYC не может быть пустым")
    @Pattern(regexp = "PENDING|IN_PROGRESS|APPROVED|REJECTED|EXPIRED",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Недопустимый статус KYC. Допустимые значения: PENDING, IN_PROGRESS, APPROVED, REJECTED, EXPIRED")
    @Schema(description = "Новый статус KYC",
            example = "APPROVED",
            allowableValues = {"PENDING", "IN_PROGRESS", "APPROVED", "REJECTED", "EXPIRED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String status; // ✅ String - будет парситься в KycStatus
}