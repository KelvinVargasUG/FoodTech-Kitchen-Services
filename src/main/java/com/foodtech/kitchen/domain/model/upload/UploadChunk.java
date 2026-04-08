package com.foodtech.kitchen.domain.model.upload;

import java.time.LocalDateTime;
import java.util.UUID;

public class UploadChunk {

    public enum ChunkStatus { PENDING, RECEIVED, INVALID }

    private final UUID id;
    private final UUID uploadId;
    private final int chunkIndex;
    private final long size;
    private final String checksum;
    private final ChunkStatus status;
    private final String filePath;
    private final LocalDateTime uploadedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private UploadChunk(UUID id, UUID uploadId, int chunkIndex, long size,
                        String checksum, ChunkStatus status, String filePath,
                        LocalDateTime uploadedAt, LocalDateTime createdAt,
                        LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.uploadId = uploadId;
        this.chunkIndex = chunkIndex;
        this.size = size;
        this.checksum = checksum;
        this.status = status;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static UploadChunk receive(UUID uploadId, int chunkIndex, long size,
                                      String checksum, String filePath, String createdBy) {
        return new UploadChunk(UUID.randomUUID(), uploadId, chunkIndex, size,
                checksum, ChunkStatus.RECEIVED, filePath,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), createdBy, createdBy);
    }

    public static UploadChunk reconstruct(UUID id, UUID uploadId, int chunkIndex, long size,
                                          String checksum, ChunkStatus status, String filePath,
                                          LocalDateTime uploadedAt, LocalDateTime createdAt,
                                          LocalDateTime updatedAt, String createdBy, String updatedBy) {
        return new UploadChunk(id, uploadId, chunkIndex, size, checksum, status, filePath,
                uploadedAt, createdAt, updatedAt, createdBy, updatedBy);
    }

    public UUID getId() { return id; }
    public UUID getUploadId() { return uploadId; }
    public int getChunkIndex() { return chunkIndex; }
    public long getSize() { return size; }
    public String getChecksum() { return checksum; }
    public ChunkStatus getStatus() { return status; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
