package com.foodtech.kitchen.application.usecases.dto;

import com.foodtech.kitchen.domain.model.upload.ErrorRecord;

import java.util.List;

public class BulkUploadResult {

    private final int created;
    private final int updated;
    private final int errorCount;
    private final List<ErrorRecord> errorRecords;

    public BulkUploadResult(int created, int updated, int errorCount, List<ErrorRecord> errorRecords) {
        this.created = created;
        this.updated = updated;
        this.errorCount = errorCount;
        this.errorRecords = errorRecords;
    }

    public int getCreated() { return created; }
    public int getUpdated() { return updated; }
    public int getErrorCount() { return errorCount; }
    public List<ErrorRecord> getErrorRecords() { return errorRecords; }
}
