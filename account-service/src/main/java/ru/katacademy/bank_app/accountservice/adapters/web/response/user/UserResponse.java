package ru.katacademy.bank_app.accountservice.adapters.web.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;

import java.time.LocalDateTime;

/**
 * Ответ с данными пользователя для API.
 * Содержит только данные для отображения, без доменных объектов.
 */
@Getter
@Builder
@Schema(description = "Ответ с данными пользователя")
public class UserResponse {

    @Schema(description = "ID пользователя", example = "123")
    private Long id;

    @Schema(description = "Полное имя пользователя", example = "Иван Иванов")
    private String fullName;

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER", allowableValues = {"USER", "ADMIN", "MODERATOR"})
    private String role;

    @Schema(description = "Дата и время создания аккаунта", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Статус KYC", example = "APPROVED", allowableValues = {"PENDING", "IN_PROGRESS", "APPROVED", "REJECTED", "EXPIRED"})
    private String kycStatus;

    /**
     * Фабричный метод для преобразования из внутреннего DTO.
     */
    public static UserResponse fromDto(UserDto userDto, String kycStatus) {
        return UserResponse.builder()
                .id(userDto.id())
                .fullName(userDto.fullName())
                .email(userDto.email())
                .role(userDto.role().name())
                .createdAt(LocalDateTime.now())
                .kycStatus(kycStatus)
                .build();
    }

    /**
     * Упрощенный фабричный метод без KYC статуса.
     */
    public static UserResponse fromDto(UserDto userDto) {
        return UserResponse.builder()
                .id(userDto.id())
                .fullName(userDto.fullName())
                .email(userDto.email())
                .role(userDto.role().name())
                .build();
    }
}