package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.ports.in.BulkUploadProductsPort;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import com.foodtech.kitchen.infrastructure.rest.dto.ChunkResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.InitUploadRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.InitUploadResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.UploadStatusResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/upload")
@ConditionalOnProperty(name = "feature.product-bulk-upload.enabled", havingValue = "true", matchIfMissing = false)
public class BulkUploadController {

    private static final String TEMPLATE_CSV =
            "nombre,precio,categoria,estacion,descripcion,estado\n" +
            "\"Hamburguesa Clásica\",12.50,\"Platos Fuertes\",HOT_KITCHEN,\"Carne de res con queso cheddar\",Activo\n" +
            "\"Mojito Cubano\",8.00,\"Bebidas\",BAR,\"Ron blanco con menta y limón\",Activo\n";

    private final BulkUploadProductsPort bulkUploadProductsPort;

    @PostMapping("/init")
    public ResponseEntity<InitUploadResponse> initUpload(
            @RequestBody InitUploadRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String principal = userDetails != null ? userDetails.getUsername() : "system";
        UploadSession session = bulkUploadProductsPort.initSession(request.getFileName(), principal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}/status")
                .buildAndExpand(session.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(new InitUploadResponse(
                        session.getId().toString(),
                        session.getUploadStatus().name()));
    }

    @PostMapping("/{id}/chunk")
    public ResponseEntity<ChunkResponse> receiveChunk(
            @PathVariable UUID id,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("checksum") String checksum,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String principal = userDetails != null ? userDetails.getUsername() : "system";
        bulkUploadProductsPort.receiveChunk(id, chunkIndex, file.getBytes(), checksum, principal);
        System.out.println("Received chunk " + chunkIndex + " for upload "
            + id + " (size: " + file.getSize() + " bytes)");
        System.out.println("response: " + ResponseEntity.ok(new ChunkResponse(id.toString(), chunkIndex, true)));
        return ResponseEntity.ok(new ChunkResponse(id.toString(), chunkIndex, true));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<UploadStatusResponse> completeUpload(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String principal = userDetails != null ? userDetails.getUsername() : "system";
        bulkUploadProductsPort.completeAndProcess(id, principal);
        UploadSession status = bulkUploadProductsPort.getStatus(id);

        return ResponseEntity.accepted()
                .body(new UploadStatusResponse(
                        id.toString(),
                        status.getUploadStatus().name(),
                        status.getProcessingStatus().name(),
                        status.getTotalRecords(),
                        status.getProcessedRecords(),
                        status.getFailedRecords()));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<UploadStatusResponse> getStatus(@PathVariable UUID id) {
        UploadSession session = bulkUploadProductsPort.getStatus(id);
        return ResponseEntity.ok(new UploadStatusResponse(
                id.toString(),
                session.getUploadStatus().name(),
                session.getProcessingStatus().name(),
                session.getTotalRecords(),
                session.getProcessedRecords(),
                session.getFailedRecords()));
    }

    @GetMapping("/{id}/errors")
    public ResponseEntity<byte[]> getErrorsCsv(@PathVariable UUID id) {
        byte[] csvBytes = bulkUploadProductsPort.getErrorsCsv(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"errores_carga_" + id + ".csv\"")
                .body(csvBytes);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] templateBytes = TEMPLATE_CSV.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"plantilla_productos.csv\"")
                .body(templateBytes);
    }
}
