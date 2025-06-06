package ru.katacademy.bank_app.accountservice.domain.mapper;

import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;

public class UserEntityMapper {
    public static UserEntity toUserEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getRole(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getCreatedAt()
        );
    }
    public static User toUser(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getRole(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt()
        );
    }
}
