package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.application.usecases.dto.BulkUploadResult;
import com.foodtech.kitchen.domain.model.upload.UploadSession;

import java.util.UUID;

public interface BulkUploadProductsPort {
    UploadSession initSession(String fileName, String createdBy);
    void receiveChunk(UUID uploadId, int chunkIndex, byte[] data, String checksum, String createdBy);
    BulkUploadResult completeAndProcess(UUID uploadId, String updatedBy);
    UploadSession getStatus(UUID uploadId);
    byte[] getErrorsCsv(UUID uploadId);
}
