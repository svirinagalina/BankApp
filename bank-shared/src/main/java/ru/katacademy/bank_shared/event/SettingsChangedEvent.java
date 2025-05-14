package ru.katacademy.bank_shared.event;

import ru.katacademy.bank_shared.valueobject.Languages;

public record SettingsChangedEvent(
    Long userId,
    boolean notificationEnabled,
    Languages language,
    boolean darkModeEnabled
) {
}
