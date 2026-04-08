package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.ports.out.UploadSessionRepository;
import com.foodtech.kitchen.domain.model.upload.ErrorRecord;
import com.foodtech.kitchen.domain.model.upload.ProductStaging;
import com.foodtech.kitchen.domain.model.upload.UploadChunk;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import com.foodtech.kitchen.domain.model.upload.UploadedFile;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UploadRepositoryAdapter implements UploadSessionRepository {

    private final UploadSessionJpaRepository sessionJpaRepo;
    private final UploadChunkJpaRepository chunkJpaRepo;
    private final UploadedFileJpaRepository fileJpaRepo;
    private final ProductStagingJpaRepository stagingJpaRepo;
    private final ErrorRecordJpaRepository errorJpaRepo;

    public UploadRepositoryAdapter(UploadSessionJpaRepository sessionJpaRepo,
                                   UploadChunkJpaRepository chunkJpaRepo,
                                   UploadedFileJpaRepository fileJpaRepo,
                                   ProductStagingJpaRepository stagingJpaRepo,
                                   ErrorRecordJpaRepository errorJpaRepo) {
        this.sessionJpaRepo = sessionJpaRepo;
        this.chunkJpaRepo = chunkJpaRepo;
        this.fileJpaRepo = fileJpaRepo;
        this.stagingJpaRepo = stagingJpaRepo;
        this.errorJpaRepo = errorJpaRepo;
    }

    @Override
    public UploadSession saveSession(UploadSession session) {
        Optional<UploadSessionEntity> existing = sessionJpaRepo.findByUuid(session.getId().toString());
        UploadSessionEntity entity = toSessionEntity(session);
        if (existing.isPresent()) {
            UploadSessionEntity updated = UploadSessionEntity.builder()
                    .id(existing.get().getId())
                    .uuid(entity.getUuid())
                    .fileName(entity.getFileName())
                    .uploadStatus(entity.getUploadStatus())
                    .processingStatus(entity.getProcessingStatus())
                    .totalRecords(entity.getTotalRecords())
                    .processedRecords(entity.getProcessedRecords())
                    .failedRecords(entity.getFailedRecords())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .createdBy(entity.getCreatedBy())
                    .updatedBy(entity.getUpdatedBy())
                    .build();
            sessionJpaRepo.save(updated);
        } else {
            sessionJpaRepo.save(entity);
        }
        return session;
    }

    @Override
    public Optional<UploadSession> findSessionById(UUID id) {
        return sessionJpaRepo.findByUuid(id.toString()).map(this::toSessionDomain);
    }

    @Override
    public UploadChunk saveChunk(UploadChunk chunk) {
        chunkJpaRepo.save(toChunkEntity(chunk));
        return chunk;
    }

    @Override
    public List<UploadChunk> findChunksByUploadId(UUID uploadId) {
        return chunkJpaRepo.findByUploadIdOrderByChunkIndex(uploadId.toString())
                .stream().map(this::toChunkDomain).collect(Collectors.toList());
    }

    @Override
    public UploadedFile saveFile(UploadedFile file) {
        fileJpaRepo.save(toFileEntity(file));
        return file;
    }

    @Override
    public Optional<UploadedFile> findFileByUploadId(UUID uploadId) {
        return fileJpaRepo.findByUploadId(uploadId.toString()).map(this::toFileDomain);
    }

    @Override
    public List<ProductStaging> saveAllStaging(List<ProductStaging> stagingRows) {
        List<ProductStagingEntity> entities = stagingRows.stream()
                .map(this::toStagingEntity).collect(Collectors.toList());
        stagingJpaRepo.saveAll(entities);
        return stagingRows;
    }

    @Override
    public List<ProductStaging> findStagingByUploadId(UUID uploadId) {
        return stagingJpaRepo.findByUploadId(uploadId.toString())
                .stream().map(this::toStagingDomain).collect(Collectors.toList());
    }

    @Override
    public ErrorRecord saveError(ErrorRecord error) {
        errorJpaRepo.save(toErrorEntity(error));
        return error;
    }

    @Override
    public List<ErrorRecord> findErrorsByUploadId(UUID uploadId) {
        return errorJpaRepo.findByUploadIdOrderByRowNumber(uploadId.toString())
                .stream().map(this::toErrorDomain).collect(Collectors.toList());
    }

    private UploadSessionEntity toSessionEntity(UploadSession s) {
        return UploadSessionEntity.builder()
                .uuid(s.getId().toString())
                .fileName(s.getFileName())
                .uploadStatus(s.getUploadStatus())
                .processingStatus(s.getProcessingStatus())
                .totalRecords(s.getTotalRecords())
                .processedRecords(s.getProcessedRecords())
                .failedRecords(s.getFailedRecords())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(s.getCreatedBy())
                .updatedBy(s.getUpdatedBy())
                .build();
    }

    private UploadSession toSessionDomain(UploadSessionEntity e) {
        return UploadSession.reconstruct(
                UUID.fromString(e.getUuid()), e.getFileName(),
                e.getUploadStatus(), e.getProcessingStatus(),
                e.getTotalRecords(), e.getProcessedRecords(), e.getFailedRecords(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }

    private UploadChunkEntity toChunkEntity(UploadChunk c) {
        return UploadChunkEntity.builder()
                .uuid(c.getId().toString())
                .uploadId(c.getUploadId().toString())
                .chunkIndex(c.getChunkIndex())
                .size(c.getSize())
                .checksum(c.getChecksum())
                .status(c.getStatus())
                .filePath(c.getFilePath())
                .uploadedAt(c.getUploadedAt())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }

    private UploadChunk toChunkDomain(UploadChunkEntity e) {
        return UploadChunk.reconstruct(
                UUID.fromString(e.getUuid()), UUID.fromString(e.getUploadId()),
                e.getChunkIndex(), e.getSize(), e.getChecksum(), e.getStatus(),
                e.getFilePath(), e.getUploadedAt(), e.getCreatedAt(), e.getUpdatedAt(),
                e.getCreatedBy(), e.getUpdatedBy());
    }

    private UploadedFileEntity toFileEntity(UploadedFile f) {
        return UploadedFileEntity.builder()
                .uuid(f.getId().toString())
                .uploadId(f.getUploadId().toString())
                .filePath(f.getFilePath())
                .fileSize(f.getFileSize())
                .checksum(f.getChecksum())
                .assembledAt(f.getAssembledAt())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .createdBy(f.getCreatedBy())
                .updatedBy(f.getUpdatedBy())
                .build();
    }

    private UploadedFile toFileDomain(UploadedFileEntity e) {
        return UploadedFile.reconstruct(
                UUID.fromString(e.getUuid()), UUID.fromString(e.getUploadId()),
                e.getFilePath(), e.getFileSize(), e.getChecksum(),
                e.getAssembledAt(), e.getCreatedAt(), e.getUpdatedAt(),
                e.getCreatedBy(), e.getUpdatedBy());
    }

    private ProductStagingEntity toStagingEntity(ProductStaging s) {
        return ProductStagingEntity.builder()
                .uuid(s.getId().toString())
                .uploadId(s.getUploadId().toString())
                .name(s.getName())
                .price(s.getPrice())
                .category(s.getCategory())
                .station(s.getStation())
                .description(s.getDescription())
                .status(s.getStatus())
                .errorMessage(s.getErrorMessage())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(s.getCreatedBy())
                .updatedBy(s.getUpdatedBy())
                .build();
    }

    private ProductStaging toStagingDomain(ProductStagingEntity e) {
        return ProductStaging.reconstruct(
                UUID.fromString(e.getUuid()), UUID.fromString(e.getUploadId()),
                e.getName(), e.getPrice(), e.getCategory(), e.getStation(),
                e.getDescription(), e.getStatus(), e.getErrorMessage(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }

    private ErrorRecordEntity toErrorEntity(ErrorRecord r) {
        return ErrorRecordEntity.builder()
                .uuid(r.getId().toString())
                .uploadId(r.getUploadId().toString())
                .rowNumber(r.getRowNumber())
                .rawData(r.getRawData())
                .errorCode(r.getErrorCode())
                .errorMessage(r.getErrorMessage())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .build();
    }

    private ErrorRecord toErrorDomain(ErrorRecordEntity e) {
        return ErrorRecord.reconstruct(
                UUID.fromString(e.getUuid()), UUID.fromString(e.getUploadId()),
                e.getRowNumber(), e.getRawData(), e.getErrorCode(), e.getErrorMessage(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }
}
