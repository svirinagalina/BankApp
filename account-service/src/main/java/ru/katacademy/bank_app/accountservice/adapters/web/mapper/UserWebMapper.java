package ru.katacademy.bank_app.accountservice.adapters.web.mapper;

import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.adapters.web.response.user.UserResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.response.user.UserRegisteredResponse;

import java.time.LocalDateTime;

public class UserWebMapper {

    private UserWebMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static UserResponse toUserResponse(UserDto dto) {
        if (dto == null) {
            return null;
        }

        return UserResponse.builder()
                .id(dto.id())
                .fullName(dto.fullName())
                .email(dto.email())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserRegisteredResponse toUserRegisteredResponse(UserDto dto) {
        if (dto == null) {
            return null;
        }

        return UserRegisteredResponse.builder()
                .userId(dto.id())
                .email(dto.email())
                .fullName(dto.fullName())
                .registeredAt(LocalDateTime.now())
                .message("Пользователь успешно зарегистрирован")
                .build();
    }
}