package ru.katacademy.bank_app.settingsservice.comand;

import lombok.Data;

@Data
public class UpdateSettingsCommand {

    private boolean notificationEnabled;

    private String language;

    private boolean darkModeEnabled;
}
