package com.foodtech.kitchen.infrastructure.rest.dto;

public class InitUploadResponse {
    private String uploadId;
    private String status;

    public InitUploadResponse() {}
    public InitUploadResponse(String uploadId, String status) {
        this.uploadId = uploadId;
        this.status = status;
    }
    public String getUploadId() { return uploadId; }
    public String getStatus() { return status; }
}
