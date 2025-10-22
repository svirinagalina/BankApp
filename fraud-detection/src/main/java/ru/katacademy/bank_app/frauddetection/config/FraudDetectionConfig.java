package ru.katacademy.bank_app.frauddetection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "fraud.detection")
@Component
@Data
public class FraudDetectionConfig {

    /**
     * Пороговая сумма для подозрительных операций (100 000)
     * Пороговое число операций в минуту (10)
     * Пороговое число операций в час (50)
     */
    private BigDecimal suspiciousAmount = BigDecimal.valueOf(100_000);
    private int maxOperationsPerMinute = 10;
    private int maxOperationsPerHour = 50;

    public FraudDetectionConfig() {
    }

    public FraudDetectionConfig(FraudDetectionConfig fraudDetectionConfig) {
        this.suspiciousAmount = fraudDetectionConfig.suspiciousAmount;
        this.maxOperationsPerMinute = fraudDetectionConfig.maxOperationsPerMinute;
        this.maxOperationsPerHour = fraudDetectionConfig.maxOperationsPerHour;
    }
}
