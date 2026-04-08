package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.domain.model.upload.ErrorRecord;
import com.foodtech.kitchen.domain.model.upload.ProductStaging;
import com.foodtech.kitchen.domain.model.upload.UploadChunk;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import com.foodtech.kitchen.domain.model.upload.UploadedFile;
import com.foodtech.kitchen.domain.model.upload.UploadChunk.ChunkStatus;
import com.foodtech.kitchen.domain.model.upload.UploadSession.ProcessingStatus;
import com.foodtech.kitchen.domain.model.upload.UploadSession.UploadStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.ErrorRecordJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.ProductStagingJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.UploadChunkJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.UploadSessionJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.UploadedFileJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ErrorRecordEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductStagingEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadChunkEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadSessionEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadedFileEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Tag;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UploadRepositoryAdapterTest {

    @Mock private UploadSessionJpaRepository sessionJpaRepo;
    @Mock private UploadChunkJpaRepository chunkJpaRepo;
    @Mock private UploadedFileJpaRepository fileJpaRepo;
    @Mock private ProductStagingJpaRepository stagingJpaRepo;
    @Mock private ErrorRecordJpaRepository errorJpaRepo;

    @InjectMocks
    private UploadRepositoryAdapter adapter;

    private UploadSession buildSession(UUID id) {
        return UploadSession.reconstruct(id, "products.csv",
                UploadStatus.UPLOADING, ProcessingStatus.PENDING,
                0, 0, 0,
                LocalDateTime.now(), LocalDateTime.now(), "user1", "user1");
    }

    private UploadSessionEntity buildSessionEntity(UUID uuid, Long jpaId) {
        return UploadSessionEntity.builder()
                .id(jpaId)
                .uuid(uuid.toString())
                .fileName("products.csv")
                .uploadStatus(UploadStatus.UPLOADING)
                .processingStatus(ProcessingStatus.PENDING)
                .totalRecords(0).processedRecords(0).failedRecords(0)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("user1").updatedBy("user1")
                .build();
    }

    private UploadChunk buildChunk(UUID uploadId) {
        return UploadChunk.reconstruct(
                UUID.randomUUID(), uploadId, 0, 1024L, "abc123",
                ChunkStatus.RECEIVED, "/tmp/chunk0",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "user1", "user1");
    }

    private UploadChunkEntity buildChunkEntity(UUID uploadId) {
        return UploadChunkEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .uploadId(uploadId.toString())
                .chunkIndex(0).size(1024L).checksum("abc123")
                .status(ChunkStatus.RECEIVED).filePath("/tmp/chunk0")
                .uploadedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("user1").updatedBy("user1")
                .build();
    }

    private UploadedFile buildFile(UUID uploadId) {
        return UploadedFile.reconstruct(
                UUID.randomUUID(), uploadId, "/tmp/file.csv", 2048L, "def456",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "user1", "user1");
    }

    private UploadedFileEntity buildFileEntity(UUID uploadId) {
        return UploadedFileEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .uploadId(uploadId.toString())
                .filePath("/tmp/file.csv").fileSize(2048L).checksum("def456")
                .assembledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("user1").updatedBy("user1")
                .build();
    }

    private ProductStaging buildStaging(UUID uploadId) {
        return ProductStaging.reconstruct(
                UUID.randomUUID(), uploadId, "Burger", "9.99", "FOOD", "GRILL",
                "A burger", "VALID", null,
                LocalDateTime.now(), LocalDateTime.now(), "user1", "user1");
    }

    private ProductStagingEntity buildStagingEntity(UUID uploadId) {
        return ProductStagingEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .uploadId(uploadId.toString())
                .name("Burger").price("9.99").category("FOOD").station("GRILL")
                .description("A burger").status("VALID").errorMessage(null)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("user1").updatedBy("user1")
                .build();
    }

    private ErrorRecord buildError(UUID uploadId) {
        return ErrorRecord.reconstruct(
                UUID.randomUUID(), uploadId, 3, "bad,row", "ERR001", "Invalid price",
                LocalDateTime.now(), LocalDateTime.now(), "user1", "user1");
    }

    private ErrorRecordEntity buildErrorEntity(UUID uploadId) {
        return ErrorRecordEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .uploadId(uploadId.toString())
                .rowNumber(3).rawData("bad,row")
                .errorCode("ERR001").errorMessage("Invalid price")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .createdBy("user1").updatedBy("user1")
                .build();
    }

    @Test
    void saveSession_newSession_savesEntityWithoutId() {
        UUID id = UUID.randomUUID();
        UploadSession session = buildSession(id);
        when(sessionJpaRepo.findByUuid(id.toString())).thenReturn(Optional.empty());

        UploadSession result = adapter.saveSession(session);

        ArgumentCaptor<UploadSessionEntity> captor = ArgumentCaptor.forClass(UploadSessionEntity.class);
        verify(sessionJpaRepo).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getUuid()).isEqualTo(id.toString());
        assertThat(result).isSameAs(session);
    }

    @Test
    void saveSession_existingSession_savesEntityPreservingJpaId() {
        UUID id = UUID.randomUUID();
        UploadSession session = buildSession(id);
        UploadSessionEntity existingEntity = buildSessionEntity(id, 42L);
        when(sessionJpaRepo.findByUuid(id.toString())).thenReturn(Optional.of(existingEntity));

        UploadSession result = adapter.saveSession(session);

        ArgumentCaptor<UploadSessionEntity> captor = ArgumentCaptor.forClass(UploadSessionEntity.class);
        verify(sessionJpaRepo).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
        assertThat(captor.getValue().getUuid()).isEqualTo(id.toString());
        assertThat(result).isSameAs(session);
    }

    @Test
    void findSessionById_found_returnsMappedDomain() {
        UUID id = UUID.randomUUID();
        UploadSessionEntity entity = buildSessionEntity(id, 1L);
        when(sessionJpaRepo.findByUuid(id.toString())).thenReturn(Optional.of(entity));

        Optional<UploadSession> result = adapter.findSessionById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getFileName()).isEqualTo("products.csv");
    }

    @Test
    void findSessionById_notFound_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(sessionJpaRepo.findByUuid(id.toString())).thenReturn(Optional.empty());

        Optional<UploadSession> result = adapter.findSessionById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void saveChunk_delegatesToJpaAndReturnsChunk() {
        UUID uploadId = UUID.randomUUID();
        UploadChunk chunk = buildChunk(uploadId);

        UploadChunk result = adapter.saveChunk(chunk);

        verify(chunkJpaRepo).save(any(UploadChunkEntity.class));
        assertThat(result).isSameAs(chunk);
    }

    @Test
    void findChunksByUploadId_returnsMappedList() {
        UUID uploadId = UUID.randomUUID();
        UploadChunkEntity entity = buildChunkEntity(uploadId);
        when(chunkJpaRepo.findByUploadIdOrderByChunkIndex(uploadId.toString()))
                .thenReturn(List.of(entity));

        List<UploadChunk> result = adapter.findChunksByUploadId(uploadId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUploadId()).isEqualTo(uploadId);
    }

    @Test
    void findChunksByUploadId_emptyList_returnsEmpty() {
        UUID uploadId = UUID.randomUUID();
        when(chunkJpaRepo.findByUploadIdOrderByChunkIndex(uploadId.toString()))
                .thenReturn(List.of());

        List<UploadChunk> result = adapter.findChunksByUploadId(uploadId);

        assertThat(result).isEmpty();
    }

    @Test
    void saveFile_delegatesToJpaAndReturnsFile() {
        UUID uploadId = UUID.randomUUID();
        UploadedFile file = buildFile(uploadId);

        UploadedFile result = adapter.saveFile(file);

        verify(fileJpaRepo).save(any(UploadedFileEntity.class));
        assertThat(result).isSameAs(file);
    }

    @Test
    void findFileByUploadId_found_returnsMappedFile() {
        UUID uploadId = UUID.randomUUID();
        UploadedFileEntity entity = buildFileEntity(uploadId);
        when(fileJpaRepo.findByUploadId(uploadId.toString())).thenReturn(Optional.of(entity));

        Optional<UploadedFile> result = adapter.findFileByUploadId(uploadId);

        assertThat(result).isPresent();
        assertThat(result.get().getUploadId()).isEqualTo(uploadId);
    }

    @Test
    void findFileByUploadId_notFound_returnsEmpty() {
        UUID uploadId = UUID.randomUUID();
        when(fileJpaRepo.findByUploadId(uploadId.toString())).thenReturn(Optional.empty());

        Optional<UploadedFile> result = adapter.findFileByUploadId(uploadId);

        assertThat(result).isEmpty();
    }

    @Test
    void saveAllStaging_savesAllAndReturnsList() {
        UUID uploadId = UUID.randomUUID();
        ProductStaging staging = buildStaging(uploadId);

        List<ProductStaging> result = adapter.saveAllStaging(List.of(staging));

        verify(stagingJpaRepo).saveAll(anyList());
        assertThat(result).containsExactly(staging);
    }

    @Test
    void saveAllStaging_emptyList_savesEmptyAndReturnsEmpty() {
        List<ProductStaging> result = adapter.saveAllStaging(List.of());

        verify(stagingJpaRepo).saveAll(anyList());
        assertThat(result).isEmpty();
    }

    @Test
    void findStagingByUploadId_returnsMappedList() {
        UUID uploadId = UUID.randomUUID();
        ProductStagingEntity entity = buildStagingEntity(uploadId);
        when(stagingJpaRepo.findByUploadId(uploadId.toString())).thenReturn(List.of(entity));

        List<ProductStaging> result = adapter.findStagingByUploadId(uploadId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUploadId()).isEqualTo(uploadId);
        assertThat(result.get(0).getName()).isEqualTo("Burger");
    }

    @Test
    void findStagingByUploadId_emptyList_returnsEmpty() {
        UUID uploadId = UUID.randomUUID();
        when(stagingJpaRepo.findByUploadId(uploadId.toString())).thenReturn(List.of());

        List<ProductStaging> result = adapter.findStagingByUploadId(uploadId);

        assertThat(result).isEmpty();
    }

    @Test
    void saveError_delegatesToJpaAndReturnsError() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord error = buildError(uploadId);

        ErrorRecord result = adapter.saveError(error);

        verify(errorJpaRepo).save(any(ErrorRecordEntity.class));
        assertThat(result).isSameAs(error);
    }

    @Test
    void findErrorsByUploadId_returnsMappedList() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecordEntity entity = buildErrorEntity(uploadId);
        when(errorJpaRepo.findByUploadIdOrderByRowNumber(uploadId.toString()))
                .thenReturn(List.of(entity));

        List<ErrorRecord> result = adapter.findErrorsByUploadId(uploadId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUploadId()).isEqualTo(uploadId);
        assertThat(result.get(0).getErrorCode()).isEqualTo("ERR001");
    }

    @Test
    void findErrorsByUploadId_emptyList_returnsEmpty() {
        UUID uploadId = UUID.randomUUID();
        when(errorJpaRepo.findByUploadIdOrderByRowNumber(uploadId.toString()))
                .thenReturn(List.of());

        List<ErrorRecord> result = adapter.findErrorsByUploadId(uploadId);

        assertThat(result).isEmpty();
    }

    @Test
    void saveChunk_entityHasCorrectFields() {
        UUID uploadId = UUID.randomUUID();
        UploadChunk chunk = buildChunk(uploadId);
        ArgumentCaptor<UploadChunkEntity> captor = ArgumentCaptor.forClass(UploadChunkEntity.class);

        adapter.saveChunk(chunk);

        verify(chunkJpaRepo).save(captor.capture());
        UploadChunkEntity captured = captor.getValue();
        assertThat(captured.getUploadId()).isEqualTo(uploadId.toString());
        assertThat(captured.getChecksum()).isEqualTo("abc123");
        assertThat(captured.getStatus()).isEqualTo(ChunkStatus.RECEIVED);
    }

    @Test
    void saveError_entityHasCorrectFields() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord error = buildError(uploadId);
        ArgumentCaptor<ErrorRecordEntity> captor = ArgumentCaptor.forClass(ErrorRecordEntity.class);

        adapter.saveError(error);

        verify(errorJpaRepo).save(captor.capture());
        ErrorRecordEntity captured = captor.getValue();
        assertThat(captured.getUploadId()).isEqualTo(uploadId.toString());
        assertThat(captured.getRowNumber()).isEqualTo(3);
        assertThat(captured.getErrorCode()).isEqualTo("ERR001");
    }
}
