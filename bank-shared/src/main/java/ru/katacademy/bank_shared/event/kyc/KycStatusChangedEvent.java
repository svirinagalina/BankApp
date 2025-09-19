package ru.katacademy.bank_shared.event.kyc;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.katacademy.bank_shared.enums.KycStatus;

import java.time.Instant;

/**
 * Событие про смену kyc-статуса пользователя
 * @param userId
 * @param status
 * @param timestamp
 * @param source
 */
public record KycStatusChangedEvent (String userId,
                                     @JsonFormat(shape = JsonFormat.Shape.STRING) KycStatus status,
                                     Instant timestamp,
                                     String source){
}
