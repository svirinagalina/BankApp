package ru.katacademy.kycservice.infrastructure.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.katacademy.kycservice.application.port.out.MinioStorage;

@Component
@Profile("local")
public class MockMinioStorage implements MinioStorage {

    @Override
    public String uploadFile(MultipartFile file) {
        // Просто возвращаем фиктивный ключ, не трогая реальные файлы
        return "mock-file-key";
    }

    @Override
    public void deleteFile(String fileKey) {
        // Ничего не делаем
    }
}
