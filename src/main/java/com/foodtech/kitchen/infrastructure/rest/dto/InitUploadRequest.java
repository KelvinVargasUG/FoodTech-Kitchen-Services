package com.foodtech.kitchen.infrastructure.rest.dto;

public class InitUploadRequest {
    private String fileName;

    public InitUploadRequest() {}
    public InitUploadRequest(String fileName) { this.fileName = fileName; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
