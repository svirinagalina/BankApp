package ru.katacademy.bank_app.settingsservice.application.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.katacademy.bank_shared.valueobject.Languages;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsDto {

    @NotNull
    private long userId;

    @NotNull
    private boolean notificationEnabled;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Languages language;

    @NotNull
    private boolean darkModeEnabled;
}
