package ru.katacademy.bank_app.frauddetection.client;

import ru.katacademy.bank_shared.event.TransferCompletedEvent;

public record FraudAnalysisMessage(
        TransferCompletedEvent transferEvent,
        AccountDto accountDto
) {}
