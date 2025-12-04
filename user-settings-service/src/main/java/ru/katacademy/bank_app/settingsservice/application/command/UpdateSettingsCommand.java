package ru.katacademy.bank_app.settingsservice.application.command;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.katacademy.bank_shared.valueobject.Languages;


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
