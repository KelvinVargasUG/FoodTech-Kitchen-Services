package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.CsvValidationException;
import com.foodtech.kitchen.application.exepcions.FileSizeLimitExceededException;
import com.foodtech.kitchen.application.exepcions.UploadSessionNotFoundException;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.ports.out.UploadSessionRepository;
import com.foodtech.kitchen.application.usecases.dto.BulkUploadResult;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.upload.ErrorRecord;
import com.foodtech.kitchen.domain.model.upload.ProductStaging;
import com.foodtech.kitchen.domain.model.upload.UploadChunk;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class BulkUploadProductsUseCaseTest {

    @Mock
    private UploadSessionRepository uploadSessionRepository;

    @Mock
    private ProductRepository productRepository;

    @TempDir
    Path tempDir;

    private BulkUploadProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BulkUploadProductsUseCase(uploadSessionRepository, productRepository, tempDir.toString());
    }

    @Test
    @DisplayName("initSession() should persist and return a new UploadSession")
    void initSession_validFileName_sessionPersistedAndReturned() {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        when(uploadSessionRepository.saveSession(any(UploadSession.class))).thenReturn(session);

        UploadSession result = useCase.initSession("productos.csv", "admin");

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("productos.csv", result.getFileName());
        verify(uploadSessionRepository).saveSession(any(UploadSession.class));
    }

    @Test
    @DisplayName("receiveChunk() should throw UploadSessionNotFoundException when uploadId unknown")
    void receiveChunk_unknownUploadId_throwsNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(uploadSessionRepository.findSessionById(unknownId)).thenReturn(Optional.empty());

        assertThrows(UploadSessionNotFoundException.class,
                () -> useCase.receiveChunk(unknownId, 0, new byte[]{1, 2, 3}, "cksum0", "admin"));
    }

    @Test
    @DisplayName("receiveChunk() should write chunk to disk and save chunk record")
    void receiveChunk_validArgs_chunkWrittenAndPersisted() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        UUID uploadId = session.getId();

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.saveChunk(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.receiveChunk(uploadId, 0, "nombre,precio\n".getBytes(), "cksum", "admin");

        verify(uploadSessionRepository).saveChunk(any(UploadChunk.class));
    }

    @Test
    @DisplayName("completeAndProcess() should throw CsvValidationException when required headers are missing")
    void completeAndProcess_missingHeaders_throwsCsvValidationException() throws Exception {
        UploadSession session = UploadSession.initiate("bad.csv", "admin");
        UUID uploadId = session.getId();

        Path uploadDir = tempDir.resolve(uploadId.toString());
        Files.createDirectories(uploadDir);
        Files.write(uploadDir.resolve("chunk_0"), "col1,col2\nvalue1,value2\n".getBytes());

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>());
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(CsvValidationException.class, () -> useCase.completeAndProcess(uploadId, "admin"));
    }

    @Test
    @DisplayName("completeAndProcess() should throw UploadSessionNotFoundException for unknown uploadId")
    void completeAndProcess_unknownId_throwsNotFoundException() {
        UUID ghostId = UUID.randomUUID();
        when(uploadSessionRepository.findSessionById(ghostId)).thenReturn(Optional.empty());

        assertThrows(UploadSessionNotFoundException.class, () -> useCase.completeAndProcess(ghostId, "admin"));
    }

    @Test
    @DisplayName("getStatus() should return session summary when uploadId exists")
    void getStatus_existingId_returnsSummary() {
        UploadSession session = UploadSession.initiate("file.csv", "admin");
        UUID uploadId = session.getId();
        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));

        UploadSession result = useCase.getStatus(uploadId);

        assertNotNull(result);
        assertEquals(uploadId, result.getId());
    }

    @Test
    @DisplayName("getStatus() should throw UploadSessionNotFoundException when uploadId unknown")
    void getStatus_unknownId_throwsNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(uploadSessionRepository.findSessionById(unknownId)).thenReturn(Optional.empty());

        assertThrows(UploadSessionNotFoundException.class, () -> useCase.getStatus(unknownId));
    }

    @Test
    @DisplayName("completeAndProcess() creates products from valid CSV and returns correct counts")
    void completeAndProcess_validCsv_createsProductsAndReturnsResult() throws Exception {
        UploadSession session = UploadSession.initiate("productos.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Coca Cola,5.50,Bebidas,BAR,Refresco,Activo\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        ProductStaging staging = ProductStaging.fromRawRow(uploadId, "Coca Cola", "5.50",
                "Bebidas", "BAR", "Refresco", "Activo", "admin");
        Product saved = Product.create("Coca Cola", ProductType.DRINK, "Bebidas", 550, "Refresco");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveAllStaging(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of(staging));
        when(productRepository.findByName("Coca Cola")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(saved);

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(1, result.getCreated());
        assertEquals(0, result.getUpdated());
        assertEquals(0, result.getErrorCount());
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("completeAndProcess() updates existing product when name already exists")
    void completeAndProcess_existingProduct_updatesAndReturnsCountOne() throws Exception {
        UploadSession session = UploadSession.initiate("update.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Lomo Saltado,15.00,Platos,HOT_KITCHEN,Plato peruano,Activo\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        ProductStaging staging = ProductStaging.fromRawRow(uploadId, "Lomo Saltado", "15.00",
                "Platos", "HOT_KITCHEN", "Plato peruano", "Activo", "admin");

        UUID existingUuid = UUID.randomUUID();
        Product existing = Product.reconstruct(existingUuid,
                "Lomo Saltado", ProductType.HOT_DISH, "Platos", 1200, ProductStatus.ACTIVE);
        Product updated = Product.reconstruct(existingUuid,
                "Lomo Saltado", ProductType.HOT_DISH, "Platos", 1500, ProductStatus.ACTIVE);

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveAllStaging(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of(staging));
        when(productRepository.findByName("Lomo Saltado")).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(updated);

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(0, result.getCreated());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getErrorCount());
    }

    @Test
    @DisplayName("completeAndProcess() persists validation errors for invalid rows and skips them")
    void completeAndProcess_withInvalidRows_persistsErrorsAndSkipsRows() throws Exception {
        UploadSession session = UploadSession.initiate("errores.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     ",5.00,Bebidas,BAR,,Activo\n" +
                     "Pasta,abc,Platos,HOT_KITCHEN,,Activo\n" +
                     "Pizza,12.00,Italiana,INVALID_STATION,,Activo\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of());
        when(uploadSessionRepository.saveError(any())).thenAnswer(inv -> inv.getArgument(0));

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(0, result.getCreated());
        assertEquals(0, result.getUpdated());
        assertEquals(3, result.getErrorCount());
        verify(uploadSessionRepository, times(3)).saveError(any());
    }

    @Test
    @DisplayName("completeAndProcess() handles row with precio <= 0 as validation error")
    void completeAndProcess_precioZeroOrNegative_isValidationError() throws Exception {
        UploadSession session = UploadSession.initiate("bad_price.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Agua,0,Bebidas,BAR,,Activo\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of());
        when(uploadSessionRepository.saveError(any())).thenAnswer(inv -> inv.getArgument(0));

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(1, result.getErrorCount());
        verify(uploadSessionRepository).saveError(any());
    }

    @Test
    @DisplayName("completeAndProcess() handles row with invalid estado as validation error")
    void completeAndProcess_invalidEstado_isValidationError() throws Exception {
        UploadSession session = UploadSession.initiate("bad_estado.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Té,3.00,Bebidas,BAR,,DESCONOCIDO\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of());
        when(uploadSessionRepository.saveError(any())).thenAnswer(inv -> inv.getArgument(0));

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(1, result.getErrorCount());
    }

    @Test
    @DisplayName("completeAndProcess() handles row with insufficient columns as validation error")
    void completeAndProcess_insufficientColumns_isValidationError() throws Exception {
        UploadSession session = UploadSession.initiate("short.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Jugo,2.00\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of());
        when(uploadSessionRepository.saveError(any())).thenAnswer(inv -> inv.getArgument(0));

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(1, result.getErrorCount());
    }

    @Test
    @DisplayName("completeAndProcess() handles COLD_KITCHEN station correctly (maps to COLD_DISH)")
    void completeAndProcess_coldKitchenStation_createsProductWithColdDishType() throws Exception {
        UploadSession session = UploadSession.initiate("cold.csv", "admin");
        UUID uploadId = session.getId();

        String csv = "nombre,precio,categoria,estacion,descripcion,estado\n" +
                     "Ensalada,8.00,Ensaladas,COLD_KITCHEN,,Inactivo\n";
        byte[] data = csv.getBytes();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length, "cksum", chunkFile.toString(), "admin");

        ProductStaging staging = ProductStaging.fromRawRow(uploadId, "Ensalada", "8.00",
                "Ensaladas", "COLD_KITCHEN", "", "Inactivo", "admin");
        Product saved = Product.create("Ensalada", ProductType.COLD_DISH, "Ensaladas", 800);

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));
        when(uploadSessionRepository.saveFile(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveSession(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.saveAllStaging(any())).thenAnswer(inv -> inv.getArgument(0));
        when(uploadSessionRepository.findStagingByUploadId(uploadId)).thenReturn(List.of(staging));
        when(productRepository.findByName("Ensalada")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(saved);

        BulkUploadResult result = useCase.completeAndProcess(uploadId, "admin");

        assertEquals(1, result.getCreated());
    }

    @Test
    @DisplayName("getErrorsCsv() returns CSV bytes with header and one row per error")
    void getErrorsCsv_withErrors_returnsFormattedCsvBytes() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord error = ErrorRecord.of(uploadId, 2, "bad,row", "VALIDATION_ERROR", "nombre vacío", "admin");

        when(uploadSessionRepository.findErrorsByUploadId(uploadId)).thenReturn(List.of(error));

        byte[] result = useCase.getErrorsCsv(uploadId);

        String csv = new String(result);
        assertTrue(csv.startsWith("fila,datos_originales,codigo_error,motivo_del_error"));
        assertTrue(csv.contains("VALIDATION_ERROR"));
        assertTrue(csv.contains("nombre vacío"));
    }

    @Test
    @DisplayName("getErrorsCsv() returns only header when there are no errors")
    void getErrorsCsv_noErrors_returnsHeaderOnly() {
        UUID uploadId = UUID.randomUUID();
        when(uploadSessionRepository.findErrorsByUploadId(uploadId)).thenReturn(List.of());

        byte[] result = useCase.getErrorsCsv(uploadId);

        String csv = new String(result);
        assertEquals("fila,datos_originales,codigo_error,motivo_del_error\n", csv);
    }

    @Test
    @DisplayName("completeAndProcess() throws FileSizeLimitExceededException when file exceeds max size (CA-05-03)")
    void completeAndProcess_fileSizeExceedsLimit_throwsFileSizeLimitExceededException() throws Exception {

        BulkUploadProductsUseCase strictUseCase = new BulkUploadProductsUseCase(
                uploadSessionRepository, productRepository, tempDir.toString(), 1L);

        UploadSession session = UploadSession.initiate("large.csv", "admin");
        UUID uploadId = session.getId();

        Path chunkFile = tempDir.resolve(uploadId.toString()).resolve("chunk_0");
        Files.createDirectories(chunkFile.getParent());
        byte[] data = "nombre,precio,categoria,estacion,descripcion,estado\n".getBytes();
        Files.write(chunkFile, data);

        UploadChunk chunk = UploadChunk.receive(uploadId, 0, data.length,
                "checksum", chunkFile.toString(), "admin");

        when(uploadSessionRepository.findSessionById(uploadId)).thenReturn(Optional.of(session));
        when(uploadSessionRepository.findChunksByUploadId(uploadId)).thenReturn(new ArrayList<>(List.of(chunk)));

        assertThrows(FileSizeLimitExceededException.class,
                () -> strictUseCase.completeAndProcess(uploadId, "admin"));
    }
}
