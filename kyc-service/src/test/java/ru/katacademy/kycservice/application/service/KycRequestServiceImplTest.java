package ru.katacademy.kycservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
<<<<<<< HEAD
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.kycservice.application.port.out.KycDocumentRepository;
=======
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;
import ru.katacademy.kycservice.application.port.out.KycEventAuditRepository;
import ru.katacademy.kycservice.application.port.out.KycEventPublisher;
>>>>>>> origin/main
import ru.katacademy.kycservice.application.port.out.KycRequestRepository;
import ru.katacademy.kycservice.domain.entity.KycRequest;
import ru.katacademy.kycservice.exception.InvalidDocumentException;
import ru.katacademy.kycservice.exception.KycAlreadyExistsException;
import ru.katacademy.kycservice.exception.KycNotFoundException;
import ru.katacademy.kycservice.infrastructure.storage.MockMinioStorage;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.katacademy.bank_shared.enums.KycStatus.APPROVED;
import static ru.katacademy.bank_shared.enums.KycStatus.PENDING;

/**
 * Тестовый класс для {@link KycRequestServiceImpl}
 * - startThrowsExceptionWhenExist: если заявка уже существует -> исключение KycAlreadyExistsException
 * - startAndCreateKycRequest: создание заявки
 * - getUserByIdWhenNotExist: если нет заявки по userId -> исключение KycNotFoundException
 * - getUserByIdWhenExist: возвращается найденная заявка с ожидаемыми полями
 * - uploadDocumentThrowsExceptionWhenEmptyFile: попытка загрузки пустого файла -> InvalidDocumentException
 * - uploadDocumentTrowsExceptionWhenBigFile: попытка загрузки большого файла -> InvalidDocumentException
 * - uploadDocumentThrowsExceptionWhenMimeFile: попытка загрузки неподдерживаемого MIME -> InvalidDocumentException
 */
@ExtendWith(MockitoExtension.class)
class KycRequestServiceImplTest {

    @Mock
    KycRequestRepository kycRequestRepository;
    @Mock
<<<<<<< HEAD
    KycDocumentRepository kycDocumentRepository;
    @Mock
    MockMinioStorage minioStorage;
    @Mock
    KafkaTemplate<String, String> kafkaTemplate;
=======
    KycEventPublisher kycEventPublisher;
    @Mock
    KycEventAuditRepository kycEventAuditRepository;
>>>>>>> origin/main
    @InjectMocks
    KycRequestServiceImpl kycRequestService;

    static final Long USER_ID = 2L;
    static final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    static final int BIG_FILE_SIZE = 5 * 1024 * 1024 + 1;
    MultipartFile emptyFile = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);
    MultipartFile bigFile = new MockMultipartFile("file", "big.pdf", "application/pdf", new byte[BIG_FILE_SIZE]);
    MultipartFile mimeFile = new MockMultipartFile("file", "mime.pdf", "text/plain", new byte[500]);

    @Test
    void startThrowsExceptionWhenExist() {
        KycRequest kycRequest = new KycRequest(id,USER_ID,PENDING,null,null);

        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(kycRequest));

        KycAlreadyExistsException thrownException = assertThrows(KycAlreadyExistsException.class, () -> {
            kycRequestService.start(USER_ID);
        });
        assertEquals("KYC request already exists for userId=" + USER_ID, thrownException.getMessage());
    }

    @Test
    void startAndCreateKycRequest() {
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        KycRequest kycRequest = new KycRequest(id,USER_ID,PENDING,null,null);

        when(kycRequestRepository.save(any(KycRequest.class))).thenReturn(kycRequest);

        KycRequest createdKycRequest = kycRequestService.start(USER_ID);

        assertEquals(PENDING, createdKycRequest.getStatus());
        assertEquals(USER_ID, createdKycRequest.getUserId());
        assertEquals(id, createdKycRequest.getId());
    }

    @Test
    void getUserByIdWhenNotExist(){
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertThrows(KycNotFoundException.class, () -> {
            kycRequestService.getByUserId(USER_ID);
        });
    }

    @Test
    void getUserByIdWhenExist(){
        KycRequest kycRequest = new KycRequest(id,USER_ID,PENDING,null,null);
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(kycRequest));

        KycRequest kyc = kycRequestService.getByUserId(USER_ID);

        assertEquals(id, kyc.getId());
        assertEquals(USER_ID, kyc.getUserId());
        assertEquals(PENDING, kyc.getStatus());
    }

    @Test
    void uploadDocumentThrowsExceptionWhenEmptyFile() {
        assertThrows(InvalidDocumentException.class, () -> {
            kycRequestService.uploadDocument(USER_ID,"passport",emptyFile);
        });
    }

    @Test
    void uploadDocumentTrowsExceptionWhenBigFile() {
        assertThrows(InvalidDocumentException.class, () -> {
            kycRequestService.uploadDocument(USER_ID,"passport",bigFile);
        });
    }

    @Test
    void uploadDocumentThrowsExceptionWhenMimeFile() {
        assertThrows(InvalidDocumentException.class, () -> {
            kycRequestService.uploadDocument(USER_ID,"passport",mimeFile);
        });
    }

    @Test
<<<<<<< HEAD
    void uploadDocumentCallMinioStorage() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        KycRequest kycRequest = new KycRequest(id, USER_ID, PENDING, null, null);
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(kycRequest));

        // Загружаем документ
        kycRequestService.uploadDocument(USER_ID, "passport", file);

        // Проверяем, что метод uploadFile вызвался ровно 1 раз с этим файлом
        verify(minioStorage, times(1)).uploadFile(file);
        verify(kycDocumentRepository, times(1)).save(any());
    }

    @Test
    void changeServicePublishesKafkaEvent() {

        KycRequest kycRequest = new KycRequest(id,USER_ID,PENDING,null,null);
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(kycRequest));
        when(kycRequestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        kycRequestService.changeStatus(USER_ID, KycStatus.APPROVED);

        verify(kafkaTemplate).send("kyc-events",USER_ID.toString(), "STATUS_APPROVED");
=======
    void changeStatusByUserIdUpdatesStatusAndPublishesEvent() {
        KycRequest existing = new KycRequest(id, USER_ID, PENDING, null, null);
        when(kycRequestRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        KycRequest saved = new KycRequest(id, USER_ID, APPROVED, null, null);
        when(kycRequestRepository.save(any(KycRequest.class))).thenReturn(saved);

        KycRequest result = kycRequestService.changeStatusByUserId(USER_ID, APPROVED, "ignored");

        assertEquals(APPROVED, result.getStatus());
        assertEquals(USER_ID, result.getUserId());

        verify(kycRequestRepository).save(any(KycRequest.class));
        verify(kycEventAuditRepository).save(id, APPROVED);

        ArgumentCaptor<KycStatusChangedEvent> captor = ArgumentCaptor.forClass(KycStatusChangedEvent.class);
        verify(kycEventPublisher).publish(captor.capture());
        KycStatusChangedEvent evt = captor.getValue();
        assertEquals(String.valueOf(USER_ID), evt.userId());
        assertEquals(APPROVED, evt.status());
>>>>>>> origin/main
    }
}
