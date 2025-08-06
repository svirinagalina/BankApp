package ru.katacademy.kycservice.application.port.out;


import org.springframework.web.multipart.MultipartFile;

public interface MinioStorage {
    String uploadFile(MultipartFile file);
}
