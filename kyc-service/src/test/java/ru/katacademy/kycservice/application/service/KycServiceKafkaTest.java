package ru.katacademy.kycservice.application.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.kycservice.application.port.out.KycDocumentRepository;
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.application.port.out.MinioStorage;
import ru.katacademy.kycservice.domain.entity.KycDocument;
import ru.katacademy.kycservice.domain.entity.KycRequest;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"kyc-events"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KycServiceKafkaTest {

    @Autowired
    private KycService kycService;

    @MockBean
    private KycRequestRepository kycRequestRepository;

    @MockBean
    private KycDocumentRepository kycDocumentRepository;

    @MockBean
    private MinioStorage minioStorage;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void shouldSendKafkaEventWhenKycIsCompleted() {
        // Подготовка данных
        UUID requestId = UUID.randomUUID();
        KycRequest request = new KycRequest(
                requestId,
                123L,
                KycStatus.PENDING,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        when(kycRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        UUID documentId = UUID.randomUUID();
        KycDocument kycDocument = new KycDocument(
                documentId,
                requestId,
                "passport",
                "file-key",
                OffsetDateTime.now()
        );
        when(kycDocumentRepository.findById(documentId.toString())).thenReturn(Optional.of(kycDocument));

        System.out.println("Kafka brokers: " + embeddedKafkaBroker.getBrokersAsString());

        kycService.completeKyc(documentId.toString());

        // Настройка consumer для проверки события
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "test-group", "true", embeddedKafkaBroker);
        var consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer())
                .createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "kyc-events");

        // Получение сообщения
        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "kyc-events");


        assertThat(record.key()).isEqualTo("123");
        assertThat(record.value()).isEqualTo("KYC_APPROVED");

        // Проверка изменения статуса KYC
        Optional<KycRequest> updatedRequest = kycRequestRepository.findById(requestId);
        assertThat(updatedRequest).isPresent();
        assertThat(updatedRequest.get().getStatus()).isEqualTo(KycStatus.APPROVED);
    }
}