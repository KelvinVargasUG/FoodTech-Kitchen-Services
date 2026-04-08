package com.foodtech.kitchen.infrastructure.rest.dto;

public class UploadStatusResponse {
    private String uploadId;
    private String uploadStatus;
    private String processingStatus;
    private int totalRecords;
    private int processedRecords;
    private int failedRecords;

    public UploadStatusResponse() {}
    public UploadStatusResponse(String uploadId, String uploadStatus, String processingStatus,
                                int totalRecords, int processedRecords, int failedRecords) {
        this.uploadId = uploadId;
        this.uploadStatus = uploadStatus;
        this.processingStatus = processingStatus;
        this.totalRecords = totalRecords;
        this.processedRecords = processedRecords;
        this.failedRecords = failedRecords;
    }
    public String getUploadId() { return uploadId; }
    public String getUploadStatus() { return uploadStatus; }
    public String getProcessingStatus() { return processingStatus; }
    public int getTotalRecords() { return totalRecords; }
    public int getProcessedRecords() { return processedRecords; }
    public int getFailedRecords() { return failedRecords; }
}
