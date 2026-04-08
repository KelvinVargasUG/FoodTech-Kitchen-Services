package com.foodtech.kitchen.domain.model.upload;

import java.time.LocalDateTime;
import java.util.UUID;

public class ErrorRecord {

    private final UUID id;
    private final UUID uploadId;
    private final int rowNumber;
    private final String rawData;
    private final String errorCode;
    private final String errorMessage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private ErrorRecord(UUID id, UUID uploadId, int rowNumber, String rawData,
                        String errorCode, String errorMessage,
                        LocalDateTime createdAt, LocalDateTime updatedAt,
                        String createdBy, String updatedBy) {
        this.id = id;
        this.uploadId = uploadId;
        this.rowNumber = rowNumber;
        this.rawData = rawData;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static ErrorRecord of(UUID uploadId, int rowNumber, String rawData,
                                 String errorCode, String errorMessage, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new ErrorRecord(UUID.randomUUID(), uploadId, rowNumber, rawData,
                errorCode, errorMessage, now, now, createdBy, createdBy);
    }

    public static ErrorRecord reconstruct(UUID id, UUID uploadId, int rowNumber, String rawData,
                                          String errorCode, String errorMessage,
                                          LocalDateTime createdAt, LocalDateTime updatedAt,
                                          String createdBy, String updatedBy) {
        return new ErrorRecord(id, uploadId, rowNumber, rawData, errorCode, errorMessage,
                createdAt, updatedAt, createdBy, updatedBy);
    }

    public UUID getId() { return id; }
    public UUID getUploadId() { return uploadId; }
    public int getRowNumber() { return rowNumber; }
    public String getRawData() { return rawData; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
