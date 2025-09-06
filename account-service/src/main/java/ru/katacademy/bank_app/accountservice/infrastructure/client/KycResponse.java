package ru.katacademy.bank_app.accountservice.infrastructure.client;

public record KycResponse(boolean verified, String documentType) {
}
