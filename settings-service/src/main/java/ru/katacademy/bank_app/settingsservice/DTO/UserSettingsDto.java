package ru.katacademy.bank_app.settingsservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsDto {
    private long userId;

    private boolean notificationEnabled;

    private String language;

    private boolean darkModeEnabled;
}
