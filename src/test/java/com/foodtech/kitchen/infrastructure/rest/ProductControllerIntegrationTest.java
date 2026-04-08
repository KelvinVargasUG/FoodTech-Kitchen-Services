package com.foodtech.kitchen.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest(properties = "feature.product-catalog.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

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
    @DisplayName("CA1: Should create product and return 201 with Location header")
    void shouldCreateProductAndReturn201() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "Hamburguesa Clásica " + System.currentTimeMillis(),
                "type", ProductType.HOT_DISH.name(),
                "category", "Platos Principales",
                "price", 1500
        );

        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("CA2: Should return 400 when mandatory field is missing")
    void shouldReturn400WhenNameIsMissing() throws Exception {
        Map<String, Object> request = Map.of(
                "type", ProductType.DRINK.name(),
                "category", "Bebidas",
                "price", 500
        );

        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CA3: Should return 409 when product name already exists")
    void shouldReturn409WhenProductNameIsDuplicate() throws Exception {
        String uniqueName = "ProductoDuplicado " + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "name", uniqueName,
                "type", ProductType.COLD_DISH.name(),
                "category", "Postres",
                "price", 800
        );

        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("HU-03 CA1: GET /api/products lists newly created active product")
    void getActiveProducts_includesCreatedProduct() throws Exception {
        String uniqueName = "ProductoActivo " + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "name", uniqueName,
                "type", ProductType.DRINK.name(),
                "category", "Bebidas",
                "price", 600
        );

        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products").with(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + uniqueName + "')].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("HU-03 CA2: PATCH deactivate sets product status to INACTIVE")
    void deactivateProduct_returnsInactiveStatus() throws Exception {
        String uniqueName = "ProductoDeactivar " + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "name", uniqueName,
                "type", ProductType.HOT_DISH.name(),
                "category", "Platos Principales",
                "price", 1200
        );

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String productId = objectMapper.readTree(responseBody).get("id").asText();

        mockMvc.perform(patch("/api/products/" + productId + "/deactivate").with(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("HU-03 CA3: Deactivated product is excluded from GET /api/products")
    void deactivatedProduct_isExcludedFromActiveList() throws Exception {
        String uniqueName = "ProductoExcluir " + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "name", uniqueName,
                "type", ProductType.COLD_DISH.name(),
                "category", "Postres",
                "price", 700
        );

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String productId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/products/" + productId + "/deactivate").with(auth()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products").with(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + productId + "')]").isEmpty());
    }

    @Test
    @DisplayName("HU-03 CA4: PATCH activate restores INACTIVE product to ACTIVE")
    void activateProduct_restoresActiveStatus() throws Exception {
        String uniqueName = "ProductoReactivar " + System.currentTimeMillis();
        Map<String, Object> request = Map.of(
                "name", uniqueName,
                "type", ProductType.DRINK.name(),
                "category", "Bebidas",
                "price", 450
        );

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String productId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(patch("/api/products/" + productId + "/deactivate").with(auth()))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/products/" + productId + "/activate").with(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("HU-03 CA5: PATCH deactivate on unknown UUID returns 404")
    void deactivateProduct_unknownUuid_returns404() throws Exception {
        mockMvc.perform(patch("/api/products/00000000-0000-0000-0000-000000000000/deactivate").with(auth()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("HU-04 CA-04-03: GET /api/products?category=X returns only products of that category")
    void getActiveProducts_filteredByCategory_returns200WithMatchingProducts() throws Exception {
        String suffix = String.valueOf(System.currentTimeMillis());
        String principalesPayload = objectMapper.writeValueAsString(Map.of(
                "name", "Lomo Saltado " + suffix,
                "type", ProductType.HOT_DISH.name(),
                "category", "Principales",
                "price", 1500
        ));
        String bebidasPayload = objectMapper.writeValueAsString(Map.of(
                "name", "Mojito " + suffix,
                "type", ProductType.DRINK.name(),
                "category", "Bebidas",
                "price", 800
        ));

        mockMvc.perform(post("/api/products").with(auth())
                        .contentType(MediaType.APPLICATION_JSON).content(principalesPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products").with(auth())
                        .contentType(MediaType.APPLICATION_JSON).content(bebidasPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products").with(auth())
                        .param("category", "Principales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Lomo Saltado " + suffix + "')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Mojito " + suffix + "')]").doesNotExist());
    }

    @Test
    @DisplayName("CA-04-05: GET /api/products responds within 2 seconds")
    void getActiveProducts_respondsWithinTwoSeconds() throws Exception {
        for (int i = 0; i < 10; i++) {
            Map<String, Object> request = Map.of(
                    "name", "PerfProduct " + System.nanoTime(),
                    "type", ProductType.HOT_DISH.name(),
                    "category", "Rendimiento",
                    "price", 100
            );
            mockMvc.perform(post("/api/products")
                            .with(auth())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        long start = System.currentTimeMillis();
        mockMvc.perform(get("/api/products").with(auth()))
                .andExpect(status().isOk());
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 2000,
                "El catálogo tardó " + elapsed + " ms en responder; el límite es 2000 ms");
    }

    @Test
    @DisplayName("CA-05-04: Concurrent bulk upload sessions do not block catalog queries")
    void bulkUploadDoesNotBlockCatalogQueries() throws Exception {
        String uniqueName = "NonBlocking " + System.currentTimeMillis();
        Map<String, Object> productRequest = Map.of(
                "name", uniqueName,
                "type", ProductType.DRINK.name(),
                "category", "Concurrencia",
                "price", 500
        );
        mockMvc.perform(post("/api/products")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());

        int threadCount = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger catalogSuccessCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    if (idx % 2 == 0) {
                        mockMvc.perform(post("/api/upload/init")
                                        .with(auth())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fileName\":\"concurrent_" + idx + ".csv\"}"))
                                .andExpect(status().isCreated());
                        successCount.incrementAndGet();
                    } else {
                        long start = System.currentTimeMillis();
                        mockMvc.perform(get("/api/products").with(auth()))
                                .andExpect(status().isOk());
                        long elapsed = System.currentTimeMillis() - start;
                        if (elapsed < 2000) {
                            catalogSuccessCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(10, TimeUnit.SECONDS);
        pool.shutdown();

        assertTrue(finished, "Las operaciones concurrentes no terminaron en 10 segundos");
        assertTrue(catalogSuccessCount.get() >= 1,
                "Las consultas al catálogo deben responder < 2s incluso con cargas masivas simultáneas");
        assertTrue(successCount.get() >= 1, "Al menos una sesión de carga masiva debe iniciarse correctamente");
    }
}
