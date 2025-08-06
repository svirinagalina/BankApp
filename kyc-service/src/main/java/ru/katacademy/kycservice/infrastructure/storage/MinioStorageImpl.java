package ru.katacademy.kycservice.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
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
 * <p>
 * Автор: Кирюшин А.А.
 * Дата: 2025-08-05
 */
@Service
public class MinioStorageImpl implements MinioStorage {

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
            System.out.println("MinIO autoCreateBucket=" + autoCreateBucket);
            if (autoCreateBucket && !minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Bucket created: " + bucketName);
            } else {
                System.out.println("Bucket already exists or autoCreate is false");
            }
        } catch (Exception e) {
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
}


