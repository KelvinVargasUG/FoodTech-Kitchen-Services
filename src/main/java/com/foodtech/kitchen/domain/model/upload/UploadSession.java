package com.foodtech.kitchen.domain.model.upload;

import java.time.LocalDateTime;
import java.util.UUID;

public class UploadSession {

    public enum UploadStatus { UPLOADING, UPLOADED, FAILED }
    public enum ProcessingStatus { PENDING, IN_PROGRESS, COMPLETED, FAILED }

    private final UUID id;
    private final String fileName;
    private final UploadStatus uploadStatus;
    private final ProcessingStatus processingStatus;
    private final int totalRecords;
    private final int processedRecords;
    private final int failedRecords;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private UploadSession(UUID id, String fileName, UploadStatus uploadStatus,
                          ProcessingStatus processingStatus, int totalRecords,
                          int processedRecords, int failedRecords,
                          LocalDateTime createdAt, LocalDateTime updatedAt,
                          String createdBy, String updatedBy) {
        this.id = id;
        this.fileName = fileName;
        this.uploadStatus = uploadStatus;
        this.processingStatus = processingStatus;
        this.totalRecords = totalRecords;
        this.processedRecords = processedRecords;
        this.failedRecords = failedRecords;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static UploadSession initiate(String fileName, String createdBy) {
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("fileName cannot be blank");
        return new UploadSession(UUID.randomUUID(), fileName,
                UploadStatus.UPLOADING, ProcessingStatus.PENDING,
                0, 0, 0,
                LocalDateTime.now(), LocalDateTime.now(), createdBy, createdBy);
    }

    public static UploadSession reconstruct(UUID id, String fileName, UploadStatus uploadStatus,
                                            ProcessingStatus processingStatus, int totalRecords,
                                            int processedRecords, int failedRecords,
                                            LocalDateTime createdAt, LocalDateTime updatedAt,
                                            String createdBy, String updatedBy) {
        return new UploadSession(id, fileName, uploadStatus, processingStatus,
                totalRecords, processedRecords, failedRecords,
                createdAt, updatedAt, createdBy, updatedBy);
    }

    public UploadSession withUploadStatus(UploadStatus status) {
        return new UploadSession(id, fileName, status, processingStatus,
                totalRecords, processedRecords, failedRecords,
                createdAt, LocalDateTime.now(), createdBy, updatedBy);
    }

    public UploadSession withProcessingResult(ProcessingStatus status, int total, int processed, int failed) {
        return new UploadSession(id, fileName, uploadStatus, status,
                total, processed, failed,
                createdAt, LocalDateTime.now(), createdBy, updatedBy);
    }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public UploadStatus getUploadStatus() { return uploadStatus; }
    public ProcessingStatus getProcessingStatus() { return processingStatus; }
    public int getTotalRecords() { return totalRecords; }
    public int getProcessedRecords() { return processedRecords; }
    public int getFailedRecords() { return failedRecords; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
