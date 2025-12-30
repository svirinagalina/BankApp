package ru.katacademy.apigateway.dto;

public class ReadinessResponse {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}