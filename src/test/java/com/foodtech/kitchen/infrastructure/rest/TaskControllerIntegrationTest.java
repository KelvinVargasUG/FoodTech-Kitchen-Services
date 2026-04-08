package com.foodtech.kitchen.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    private String authHeaderValue;

    @BeforeEach
    void setUp() throws Exception {
        authHeaderValue = "Bearer " + tokenGenerator.generateToken("test-user");

        Map<String, Object> orderBar1 = Map.of(
            "tableNumber", "A1",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Coca Cola", "type", "DRINK", "price", 3)
            )
        );
        mockMvc.perform(post("/api/orders")
            .with(auth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderBar1)));

        Map<String, Object> orderBar2 = Map.of(
            "tableNumber", "A2",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Sprite", "type", "DRINK", "price", 3)
            )
        );
        mockMvc.perform(post("/api/orders")
            .with(auth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderBar2)));

        Map<String, Object> orderHotKitchen = Map.of(
            "tableNumber", "B1",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Pizza", "type", "HOT_DISH", "price", 12)
            )
        );
        mockMvc.perform(post("/api/orders")
            .with(auth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderHotKitchen)));
    }

    private RequestPostProcessor auth() {
        return request -> {
            request.addHeader("Authorization", authHeaderValue);
            return request;
        };
    }

    @Test
    @DisplayName("Scenario 1: Should return only tasks for specified station")
    void shouldReturnOnlyTasksForSpecifiedStation() throws Exception {

        mockMvc.perform(get("/api/tasks/station/BAR").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").exists())  
            .andExpect(jsonPath("$[0].station").value("BAR"))
            .andExpect(jsonPath("$[0].tableNumber").exists())
            .andExpect(jsonPath("$[0].products").isArray())
            .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @DisplayName("Scenario 2: Should return empty list when no tasks for station")
    void shouldReturnEmptyListWhenNoTasksForStation() throws Exception {

        mockMvc.perform(get("/api/tasks/station/COLD_KITCHEN").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Scenario 3: Should include complete task information with createdAt timestamp")
    void shouldIncludeCompleteTaskInformationWithTimestamp() throws Exception {

        mockMvc.perform(get("/api/tasks/station/BAR").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tableNumber").exists())
            .andExpect(jsonPath("$[0].station").value("BAR"))
            .andExpect(jsonPath("$[0].products").isArray())
            .andExpect(jsonPath("$[0].products[0].name").exists())
            .andExpect(jsonPath("$[0].products[0].type").exists())
            .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @DisplayName("Scenario 4: Should return 400 when invalid station")
    void shouldReturn400WhenInvalidStation() throws Exception {

        mockMvc.perform(get("/api/tasks/station/INVALID_STATION").with(auth()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("HU-003 Scenario 1: Should start task preparation and update status")
    @org.springframework.transaction.annotation.Transactional
    void shouldStartTaskPreparation() throws Exception {

        List<Task> allTasks = taskRepository.findAll();
        Long taskId = allTasks.get(0).getId();

        mockMvc.perform(patch("/api/tasks/" + taskId + "/start").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.status").value("IN_PREPARATION"))
            .andExpect(jsonPath("$.startedAt").exists());
    }

    @Test
    @DisplayName("HU-003 Scenario 3: Should return only completed tasks when filtering by status")
    @org.springframework.transaction.annotation.Transactional
    void shouldReturnOnlyCompletedTasksForStation() throws Exception {

        List<Task> barTasks = taskRepository.findByStation(Station.BAR);

        Task task1 = barTasks.get(0);
        Task task2 = barTasks.get(1);

        task1.start();
        task1.complete();
        taskRepository.save(task1);

        task2.start();
        task2.complete();
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks/station/BAR?status=COMPLETED").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].status").value("COMPLETED"))
            .andExpect(jsonPath("$[0].startedAt").exists())
            .andExpect(jsonPath("$[0].completedAt").exists())
            .andExpect(jsonPath("$[1].status").value("COMPLETED"))
            .andExpect(jsonPath("$[1].startedAt").exists())
            .andExpect(jsonPath("$[1].completedAt").exists());
    }

    @Test
    @DisplayName("HU-003 Scenario 5: Should return order status based on task states")
    @org.springframework.transaction.annotation.Transactional
    void shouldReturnOrderStatusBasedOnTaskStates() throws Exception {

        String orderRequest = objectMapper.writeValueAsString(Map.of(
            "tableNumber", "A1",
            "customerName", "Cliente Test",
            "customerEmail", "test@test.com",
            "products", List.of(
                Map.of("name", "Coca Cola", "type", "DRINK", "price", 3),
                Map.of("name", "Sprite", "type", "DRINK", "price", 3),
                Map.of("name", "Pizza", "type", "HOT_DISH", "price", 12),
                Map.of("name", "Ensalada", "type", "COLD_DISH", "price", 7)
            )
        ));

        mockMvc.perform(post("/api/orders")
            .with(auth())
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderRequest))
            .andExpect(status().isCreated());

        List<Task> allTasks = taskRepository.findAll();
        Long orderId = allTasks.get(allTasks.size() - 1).getOrderId();
        List<Task> orderTasks = taskRepository.findByOrderId(orderId);

        assertEquals(3, orderTasks.size());

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(patch("/api/tasks/" + orderTasks.get(0).getId() + "/start").with(auth()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PREPARATION"));

        Task task0 = taskRepository.findById(orderTasks.get(0).getId()).get();
        task0.complete();
        taskRepository.save(task0);

        Task task1 = taskRepository.findById(orderTasks.get(1).getId()).get();
        task1.start();
        task1.complete();
        taskRepository.save(task1);

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PREPARATION"));

        mockMvc.perform(patch("/api/tasks/" + orderTasks.get(2).getId() + "/start").with(auth()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PREPARATION"));

        Task task2 = taskRepository.findById(orderTasks.get(2).getId()).get();
        task2.complete();
        taskRepository.save(task2);

        mockMvc.perform(get("/api/orders/" + orderId + "/status").with(auth()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
