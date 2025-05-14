package ru.katacademy.bank_app.settingsservice.application.command;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.katacademy.bank_shared.valueobject.Languages;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSettingsCommand {

    @NotNull
    private Boolean notificationEnabled;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Languages language;

    @NotNull
    private Boolean darkModeEnabled;
}
