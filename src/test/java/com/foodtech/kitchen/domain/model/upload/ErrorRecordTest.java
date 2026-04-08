package com.foodtech.kitchen.domain.model.upload;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Tag;

@Tag("unit")
class ErrorRecordTest {

    @Test
    void of_setsAllFieldsAndGeneratesId() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord error = ErrorRecord.of(uploadId, 5, "bad,row,data",
                "ERR_PRICE", "Price is invalid", "user1");

        assertThat(error.getId()).isNotNull();
        assertThat(error.getUploadId()).isEqualTo(uploadId);
        assertThat(error.getRowNumber()).isEqualTo(5);
        assertThat(error.getRawData()).isEqualTo("bad,row,data");
        assertThat(error.getErrorCode()).isEqualTo("ERR_PRICE");
        assertThat(error.getErrorMessage()).isEqualTo("Price is invalid");
        assertThat(error.getCreatedBy()).isEqualTo("user1");
        assertThat(error.getUpdatedBy()).isEqualTo("user1");
        assertThat(error.getCreatedAt()).isNotNull();
        assertThat(error.getUpdatedAt()).isNotNull();
    }

    @Test
    void of_eachCallGeneratesUniqueId() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord e1 = ErrorRecord.of(uploadId, 1, "data1", "E001", "msg1", "u");
        ErrorRecord e2 = ErrorRecord.of(uploadId, 2, "data2", "E002", "msg2", "u");

        assertThat(e1.getId()).isNotEqualTo(e2.getId());
    }

    @Test
    void reconstruct_preservesAllFields() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        LocalDateTime updated = LocalDateTime.now();

        ErrorRecord error = ErrorRecord.reconstruct(id, uploadId,
                10, "raw;data", "ERR_NULL", "Field is null",
                created, updated, "admin", "system");

        assertThat(error.getId()).isEqualTo(id);
        assertThat(error.getUploadId()).isEqualTo(uploadId);
        assertThat(error.getRowNumber()).isEqualTo(10);
        assertThat(error.getRawData()).isEqualTo("raw;data");
        assertThat(error.getErrorCode()).isEqualTo("ERR_NULL");
        assertThat(error.getErrorMessage()).isEqualTo("Field is null");
        assertThat(error.getCreatedAt()).isEqualTo(created);
        assertThat(error.getUpdatedAt()).isEqualTo(updated);
        assertThat(error.getCreatedBy()).isEqualTo("admin");
        assertThat(error.getUpdatedBy()).isEqualTo("system");
    }

    @Test
    void of_rowNumberIsPreserved() {
        UUID uploadId = UUID.randomUUID();
        ErrorRecord error = ErrorRecord.of(uploadId, 42, "data", "E", "m", "u");

        assertThat(error.getRowNumber()).isEqualTo(42);
    }
}
