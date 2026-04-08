package com.foodtech.kitchen.application.exepcions;

public class UploadSessionNotFoundException extends RuntimeException {
    public UploadSessionNotFoundException(String uploadId) {
        super("Upload session not found: " + uploadId);
    }
}
