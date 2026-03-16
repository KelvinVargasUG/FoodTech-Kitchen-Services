package com.foodtech.kitchen.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenGenerator tokenGenerator;

    private String authHeaderValue;

    @BeforeEach
    void setUp() {
        authHeaderValue = "Bearer " + tokenGenerator.generateToken("test-user");
    }

    private RequestPostProcessor auth() {
        return request -> {
            request.addHeader("Authorization", authHeaderValue);
            return request;
        };
    }

    @Test
    @DisplayName("Should create order and return 201 with task count")
    void shouldCreateOrderAndReturn201() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "tableNumber", "A1",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Coca Cola", "type", "DRINK", "price", 3)
            )
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(auth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableNumber").value("A1"))
                .andExpect(jsonPath("$.tasksCreated").value(1))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should create order with mixed products")
    void shouldCreateOrderWithMixedProducts() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "tableNumber", "B2",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Coca Cola", "type", "DRINK", "price", 3),
                Map.of("name", "Pizza", "type", "HOT_DISH", "price", 12)
            )
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(auth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableNumber").value("B2"))
                .andExpect(jsonPath("$.tasksCreated").value(2));
    }

    @Test
    @DisplayName("Should reject order without products")
    void shouldRejectOrderWithoutProducts() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "tableNumber", "C3",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of()
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(auth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should reject order without table number")
    void shouldRejectOrderWithoutTableNumber() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "tableNumber", "",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Coca Cola", "type", "DRINK", "price", 3)
            )
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(auth())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should delete order and return 204")
    void shouldDeleteOrderAndReturn204() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "tableNumber", "D4",
            "customerName", "Cliente Delete",
            "customerEmail", "delete@test.com",
            "products", List.of(
                Map.of("name", "Jugo", "type", "DRINK", "price", 4)
            )
        );

        mockMvc.perform(post("/api/orders")
            .with(auth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // Obtener id creado desde endpoint existente de tareas
        String tasksResponse = mockMvc.perform(get("/api/tasks/station/BAR").with(auth()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long orderId = null;
        JsonNode tasksNode = objectMapper.readTree(tasksResponse);
        for (JsonNode taskNode : tasksNode) {
            if ("D4".equals(taskNode.get("tableNumber").asText())) {
                orderId = taskNode.get("orderId").asLong();
                break;
            }
        }

        if (orderId == null) {
            throw new IllegalStateException("Expected order for table D4 was not created");
        }

        // When & Then
        mockMvc.perform(delete("/api/orders/" + orderId).with(auth()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existing order")
    void shouldReturn404WhenDeletingNonExistingOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/999999").with(auth()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found"));
    }
}