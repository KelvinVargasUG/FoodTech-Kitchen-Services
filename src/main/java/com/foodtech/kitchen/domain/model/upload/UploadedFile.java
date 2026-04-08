package com.foodtech.kitchen.domain.model.upload;

import java.time.LocalDateTime;
import java.util.UUID;

public class UploadedFile {

    private final UUID id;
    private final UUID uploadId;
    private final String filePath;
    private final long fileSize;
    private final String checksum;
    private final LocalDateTime assembledAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private UploadedFile(UUID id, UUID uploadId, String filePath, long fileSize,
                         String checksum, LocalDateTime assembledAt,
                         LocalDateTime createdAt, LocalDateTime updatedAt,
                         String createdBy, String updatedBy) {
        this.id = id;
        this.uploadId = uploadId;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.checksum = checksum;
        this.assembledAt = assembledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static UploadedFile assemble(UUID uploadId, String filePath, long fileSize,
                                        String checksum, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new UploadedFile(UUID.randomUUID(), uploadId, filePath, fileSize,
                checksum, now, now, now, createdBy, createdBy);
    }

    public static UploadedFile reconstruct(UUID id, UUID uploadId, String filePath, long fileSize,
                                           String checksum, LocalDateTime assembledAt,
                                           LocalDateTime createdAt, LocalDateTime updatedAt,
                                           String createdBy, String updatedBy) {
        return new UploadedFile(id, uploadId, filePath, fileSize, checksum,
                assembledAt, createdAt, updatedAt, createdBy, updatedBy);
    }

    public UUID getId() { return id; }
    public UUID getUploadId() { return uploadId; }
    public String getFilePath() { return filePath; }
    public long getFileSize() { return fileSize; }
    public String getChecksum() { return checksum; }
    public LocalDateTime getAssembledAt() { return assembledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
