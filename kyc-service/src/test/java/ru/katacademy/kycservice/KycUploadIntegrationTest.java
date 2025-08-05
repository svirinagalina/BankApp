package ru.katacademy.kycservice;

import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.katacademy.kycservice.domain.enumtype.KycStatus;
import ru.katacademy.kycservice.infrastructure.persistence.entity.KycRequestEntity;
import ru.katacademy.kycservice.infrastructure.repository.KycRequestJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KycUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KycRequestJpaRepository kycRepository;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Test
    void uploadDocument_createsKycRecordAndStoresFileInMinio() throws Exception {
        // given
        String userId = "999";
        String documentType = "passport";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png",
                getClass().getResourceAsStream("/files/test.png"));

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/kyc/verify")
                        .file(file)
                        .param("userId", userId)
                        .param("documentType", documentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // then - check DB
        List<KycRequestEntity> all = kycRepository.findAll();
        assertThat(all).hasSize(1);

        KycRequestEntity saved = all.get(0);
        assertThat(saved.getUserId()).isEqualTo(Long.valueOf(userId));
        assertThat(saved.getDocumentType()).isEqualTo(documentType);
        assertThat(saved.getFileKey()).isNotEmpty();
        assertThat(saved.getStatus()).isEqualTo(KycStatus.PENDING); // Статус проверяется только в базе

        // then - check MinIO
        boolean exists = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(saved.getFileKey())
                        .build()
        ) != null;

        assertThat(exists).isTrue();
    }
}
