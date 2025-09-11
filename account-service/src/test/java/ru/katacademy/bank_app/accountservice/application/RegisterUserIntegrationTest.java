package ru.katacademy.bank_app.accountservice.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.bank_app.accountservice.application.dto.KycRequestDTO;
import ru.katacademy.bank_shared.enums.KycStatus;
import ru.katacademy.bank_app.accountservice.infrastructure.client.KycClient;
import ru.katacademy.bank_shared.exception.GlobalExceptionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RegisterUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KycClient kycClient;

    private String userJson(String fullName, String email, String password) {
        return String.format("""
                {
                  "fullName": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """, fullName, email, password);
    }

    @Test
    void register_success_whenKycApproved() throws Exception {
        when(kycClient.getKyc(any()))
                .thenReturn(new KycRequestDTO(1L, KycStatus.APPROVED));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson("Test Testov", "success@test.com", "Password123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("success@test.com"))
                .andExpect(jsonPath("$.fullName").value("Test Testov"));
    }

    @Test
    void register_fails_whenKycPending() throws Exception {
        when(kycClient.getKyc(any()))
                .thenReturn(new KycRequestDTO(1L, KycStatus.PENDING));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson("Test Testov", "pending@test.com", "Password123")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User is not KYC-verified"));
    }

    @Test
    void register_fails_whenKycRejected() throws Exception {
        when(kycClient.getKyc(any()))
                .thenReturn(new KycRequestDTO(1L, KycStatus.REJECTED));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson("Test Testov", "rejected@test.com", "Password123")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User is not KYC-verified"));
    }
}