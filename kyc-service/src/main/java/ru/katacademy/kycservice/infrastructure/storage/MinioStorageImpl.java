package ru.katacademy.kycservice.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.port.out.MinioStorage;

import java.util.UUID;

/**
 * Сервис для взаимодействия с хранилищем MinIO (загрузка файлов и инициализация bucket).
 * <p>
 * Поля:
 * - bucketName: название bucket'а в MinIO, куда загружаются файлы
 * - autoCreateBucket: флаг, указывающий, нужно ли автоматически создавать bucket при инициализации
 * <p>
 * - minioClient: клиент MinIO для выполнения операций с объектами и bucket'ами
 * Методы:
 * - init(): инициализирует bucket при старте приложения, если он не существует и включена автонастройка
 * - uploadFile(MultipartFile file): загружает файл в MinIO, возвращает сгенерированный уникальный ключ (fileKey)
 * - deleteFile(String): попытка удалить объект по ключу
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Service
public class MinioStorageImpl implements MinioStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageImpl.class);

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.auto-create-bucket:true}")
    private boolean autoCreateBucket;

    private final MinioClient minioClient;

    public MinioStorageImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("MinIO autoCreateBucket={}, bucket={}", autoCreateBucket, bucketName);
            if (autoCreateBucket && !minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket created: {}", bucketName);
            } else {
                log.info("Bucket already exists or autoCreate is false, bucket={}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", bucketName, e);
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }


    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileKey = UUID.randomUUID() + "_" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileKey;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to delete object from MinIO: bucket={}, key={}", bucketName, fileKey, e);
        }
    }
}


