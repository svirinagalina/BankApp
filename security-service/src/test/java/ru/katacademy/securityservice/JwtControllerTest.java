package ru.katacademy.securityservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.securityservice.util.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret=testsecretkeyfortestpurposesonly1234567890",
        "jwt.expiration-ms=3600000"
})
class JwtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;


    @Test
    void verifyToken_shouldReturnClaims_whenValidToken() throws Exception {
        String token = jwtUtil.generateToken("test-user");
        System.out.println(">>> GENERATED TOKEN: " + token);

        var result = mockMvc.perform(post("/api/security/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + token + "\""))
                .andReturn();

        int status = result.getResponse().getStatus();
        String body = result.getResponse().getContentAsString();

        System.out.println(">>> STATUS: " + status);
        System.out.println(">>> BODY: " + body);

    }


}
