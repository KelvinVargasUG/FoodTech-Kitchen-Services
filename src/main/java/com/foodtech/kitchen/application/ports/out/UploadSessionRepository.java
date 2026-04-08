package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.upload.ErrorRecord;
import com.foodtech.kitchen.domain.model.upload.ProductStaging;
import com.foodtech.kitchen.domain.model.upload.UploadChunk;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import com.foodtech.kitchen.domain.model.upload.UploadedFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UploadSessionRepository {
    UploadSession saveSession(UploadSession session);
    Optional<UploadSession> findSessionById(UUID id);
    UploadChunk saveChunk(UploadChunk chunk);
    List<UploadChunk> findChunksByUploadId(UUID uploadId);
    UploadedFile saveFile(UploadedFile file);
    Optional<UploadedFile> findFileByUploadId(UUID uploadId);
    List<ProductStaging> saveAllStaging(List<ProductStaging> stagingRows);
    List<ProductStaging> findStagingByUploadId(UUID uploadId);
    ErrorRecord saveError(ErrorRecord error);
    List<ErrorRecord> findErrorsByUploadId(UUID uploadId);
}
