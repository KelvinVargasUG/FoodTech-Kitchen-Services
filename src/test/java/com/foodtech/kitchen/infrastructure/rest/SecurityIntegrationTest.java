package com.foodtech.kitchen.infrastructure.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.foodtech.kitchen.infrastructure.security.JwtTokenGenerator;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    @DisplayName("RED: Protected endpoint without token returns 401")
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/tasks/station/BAR"))
                .andExpect(status().isUnauthorized());
    }

        @Test
        @DisplayName("RED: Protected endpoint with valid token returns 200")
        void protectedEndpoint_withValidToken_returns200() throws Exception {
        String registerBody = "{\"username\":\"auth-user\",\"email\":\"auth-user@example.com\",\"password\":\"abc123\"}";
        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerBody))
            .andExpect(status().isCreated());

        String loginBody = "{\"identifier\":\"auth-user@example.com\",\"password\":\"abc123\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = json.get("token").asText();

        mockMvc.perform(get("/api/tasks/station/BAR")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
        }

    @Test
    @DisplayName("RED: Protected endpoint with expired token returns 401")
    void protectedEndpoint_withExpiredToken_returns401() throws Exception {
        Instant fixedInstant = Instant.parse("2020-01-01T00:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        JwtTokenGenerator generator = new JwtTokenGenerator(jwtSecret, 1L, fixedClock);
        String expiredToken = generator.generateToken("auth-user");

        mockMvc.perform(get("/api/tasks/station/BAR")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RED: Protected endpoint with malformed token returns 401")
    void protectedEndpoint_withMalformedToken_returns401() throws Exception {
        mockMvc.perform(get("/api/tasks/station/BAR")
                .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("RED: Protected endpoint with invalid signature returns 401")
    void protectedEndpoint_withInvalidSignature_returns401() throws Exception {
        Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        JwtTokenGenerator generator = new JwtTokenGenerator(
                "different-secret-for-test-signature-1234567890",
                3600L,
                fixedClock
        );
        String invalidSignatureToken = generator.generateToken("auth-user");

        mockMvc.perform(get("/api/tasks/station/BAR")
                .header("Authorization", "Bearer " + invalidSignatureToken))
                .andExpect(status().isUnauthorized());
    }
}
