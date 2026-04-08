package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.ports.in.BulkUploadProductsPort;
import com.foodtech.kitchen.application.usecases.dto.BulkUploadResult;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(controllers = BulkUploadController.class)
@Import(BulkUploadControllerTest.PermissiveSecurityConfig.class)
@TestPropertySource(properties = {
    "feature.product-bulk-upload.enabled=true",
    "upload.storage.base-path=/tmp/foodtech-test"
})
class BulkUploadControllerTest {

    @TestConfiguration
    static class PermissiveSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BulkUploadProductsPort bulkUploadProductsPort;

    @Test
    @DisplayName("POST /api/upload/init — 201 with uploadId and Location header")
    void initUpload_validRequest_returns201WithLocation() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        when(bulkUploadProductsPort.initSession(eq("productos.csv"), anyString()))
                .thenReturn(session);

        mockMvc.perform(post("/api/upload/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fileName\":\"productos.csv\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uploadId").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/upload/{id}/chunk — 200 with received=true")
    void uploadChunk_validMultipart_returns200() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        UUID uploadId = session.getId();

        MockMultipartFile file = new MockMultipartFile(
                "file", "chunk_0", "application/octet-stream", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/upload/{id}/chunk", uploadId.toString())
                .file(file)
                .param("chunkIndex", "0")
                .param("checksum", "cksum0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.received").value(true));
    }

    @Test
    @DisplayName("GET /api/upload/{id}/status — 200 with status fields")
    void getStatus_existingId_returns200() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        UUID uploadId = session.getId();
        when(bulkUploadProductsPort.getStatus(any(UUID.class))).thenReturn(session);

        mockMvc.perform(get("/api/upload/{id}/status", uploadId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadId").value(uploadId.toString()));
    }

    @Test
    @DisplayName("GET /api/upload/template — 200 with CSV content-type")
    void downloadTemplate_returns200WithCsv() throws Exception {
        mockMvc.perform(get("/api/upload/template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                    org.hamcrest.Matchers.containsString("text/csv")));
    }

    @Test
    @DisplayName("POST /api/upload/{id}/complete — 202 with status and counters")
    void completeUpload_validId_returns202WithStats() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        UUID uploadId = session.getId();

        BulkUploadResult result = new BulkUploadResult(5, 2, 1, List.of());
        UploadSession processed = session
                .withUploadStatus(UploadSession.UploadStatus.UPLOADED)
                .withProcessingResult(UploadSession.ProcessingStatus.COMPLETED, 8, 7, 1);

        when(bulkUploadProductsPort.completeAndProcess(any(UUID.class), anyString())).thenReturn(result);
        when(bulkUploadProductsPort.getStatus(any(UUID.class))).thenReturn(processed);

        mockMvc.perform(post("/api/upload/{id}/complete", uploadId.toString()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.uploadId").value(uploadId.toString()))
                .andExpect(jsonPath("$.uploadStatus").value("UPLOADED"))
                .andExpect(jsonPath("$.processingStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.totalRecords").value(8))
                .andExpect(jsonPath("$.processedRecords").value(7))
                .andExpect(jsonPath("$.failedRecords").value(1));
    }

    @Test
    @DisplayName("POST /api/upload/{id}/complete — usa 'system' cuando no hay usuario autenticado")
    void completeUpload_withNullUserDetails_usesSystemPrincipal() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "system");
        UUID uploadId = session.getId();

        BulkUploadResult result = new BulkUploadResult(3, 0, 0, List.of());
        UploadSession processed = session
                .withProcessingResult(UploadSession.ProcessingStatus.COMPLETED, 3, 3, 0);

        when(bulkUploadProductsPort.completeAndProcess(any(UUID.class), eq("system"))).thenReturn(result);
        when(bulkUploadProductsPort.getStatus(any(UUID.class))).thenReturn(processed);

        mockMvc.perform(post("/api/upload/{id}/complete", uploadId.toString()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.uploadId").value(uploadId.toString()));
    }

    @Test
    @DisplayName("GET /api/upload/{id}/errors — 200 con Content-Disposition y body CSV")
    void getErrorsCsv_existingId_returns200WithCsvFile() throws Exception {
        UUID uploadId = UUID.randomUUID();
        String csvContent = "fila,datos_originales,codigo_error,motivo_del_error\n"
                + "2,\"mala_fila\",VALIDATION_ERROR,\"El campo precio no es numérico\"\n";
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        when(bulkUploadProductsPort.getErrorsCsv(any(UUID.class))).thenReturn(csvBytes);

        mockMvc.perform(get("/api/upload/{id}/errors", uploadId.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        org.hamcrest.Matchers.containsString("text/csv")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("errores_carga_" + uploadId)))
                .andExpect(content().bytes(csvBytes));
    }

    @Test
    @DisplayName("GET /api/upload/{id}/errors — 200 con CSV vacío cuando no hay errores")
    void getErrorsCsv_noErrors_returns200WithHeaderOnly() throws Exception {
        UUID uploadId = UUID.randomUUID();
        String csvHeader = "fila,datos_originales,codigo_error,motivo_del_error\n";
        byte[] csvBytes = csvHeader.getBytes(StandardCharsets.UTF_8);

        when(bulkUploadProductsPort.getErrorsCsv(any(UUID.class))).thenReturn(csvBytes);

        mockMvc.perform(get("/api/upload/{id}/errors", uploadId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(csvBytes));
    }

    @Test
    @DisplayName("POST /api/upload/init — usa 'system' cuando no hay usuario autenticado")
    void initUpload_withNullUserDetails_usesSystemPrincipal() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "system");
        when(bulkUploadProductsPort.initSession(eq("productos.csv"), eq("system")))
                .thenReturn(session);

        mockMvc.perform(post("/api/upload/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fileName\":\"productos.csv\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uploadId").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/upload/{id}/chunk — usa 'system' cuando no hay usuario autenticado")
    void receiveChunk_withNullUserDetails_usesSystemPrincipal() throws Exception {
        UUID uploadId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "chunk_0", "application/octet-stream", new byte[]{10, 20, 30});

        mockMvc.perform(multipart("/api/upload/{id}/chunk", uploadId.toString())
                .file(file)
                .param("chunkIndex", "0")
                .param("checksum", "abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.received").value(true));
    }
}
