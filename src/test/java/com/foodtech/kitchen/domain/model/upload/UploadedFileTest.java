package com.foodtech.kitchen.domain.model.upload;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Tag;

@Tag("unit")
class UploadedFileTest {

    @Test
    void assemble_setsAllFieldsAndGeneratesId() {
        UUID uploadId = UUID.randomUUID();
        UploadedFile file = UploadedFile.assemble(uploadId, "/storage/file.csv", 4096L,
                "sha256abc", "user1");

        assertThat(file.getId()).isNotNull();
        assertThat(file.getUploadId()).isEqualTo(uploadId);
        assertThat(file.getFilePath()).isEqualTo("/storage/file.csv");
        assertThat(file.getFileSize()).isEqualTo(4096L);
        assertThat(file.getChecksum()).isEqualTo("sha256abc");
        assertThat(file.getAssembledAt()).isNotNull();
        assertThat(file.getCreatedAt()).isNotNull();
        assertThat(file.getUpdatedAt()).isNotNull();
        assertThat(file.getCreatedBy()).isEqualTo("user1");
        assertThat(file.getUpdatedBy()).isEqualTo("user1");
    }

    @Test
    void assemble_eachCallGeneratesUniqueId() {
        UUID uploadId = UUID.randomUUID();
        UploadedFile f1 = UploadedFile.assemble(uploadId, "/p1", 100L, "cs1", "u");
        UploadedFile f2 = UploadedFile.assemble(uploadId, "/p2", 200L, "cs2", "u");

        assertThat(f1.getId()).isNotEqualTo(f2.getId());
    }

    @Test
    void reconstruct_preservesAllFields() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime assembled = LocalDateTime.now().minusMinutes(3);
        LocalDateTime created = LocalDateTime.now().minusMinutes(5);
        LocalDateTime updated = LocalDateTime.now();

        UploadedFile file = UploadedFile.reconstruct(id, uploadId, "/files/output.csv", 8192L,
                "sha256def", assembled, created, updated, "admin", "system");

        assertThat(file.getId()).isEqualTo(id);
        assertThat(file.getUploadId()).isEqualTo(uploadId);
        assertThat(file.getFilePath()).isEqualTo("/files/output.csv");
        assertThat(file.getFileSize()).isEqualTo(8192L);
        assertThat(file.getChecksum()).isEqualTo("sha256def");
        assertThat(file.getAssembledAt()).isEqualTo(assembled);
        assertThat(file.getCreatedAt()).isEqualTo(created);
        assertThat(file.getUpdatedAt()).isEqualTo(updated);
        assertThat(file.getCreatedBy()).isEqualTo("admin");
        assertThat(file.getUpdatedBy()).isEqualTo("system");
    }
}
