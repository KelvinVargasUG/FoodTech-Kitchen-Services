package com.foodtech.kitchen.domain.model.upload;

import com.foodtech.kitchen.domain.model.upload.UploadChunk.ChunkStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Tag;

@Tag("unit")
class UploadChunkTest {

    @Test
    void receive_setsStatusReceivedAndGeneratesId() {
        UUID uploadId = UUID.randomUUID();
        UploadChunk chunk = UploadChunk.receive(uploadId, 0, 512L, "checksum123", "/tmp/part0", "user");

        assertThat(chunk.getId()).isNotNull();
        assertThat(chunk.getUploadId()).isEqualTo(uploadId);
        assertThat(chunk.getChunkIndex()).isZero();
        assertThat(chunk.getSize()).isEqualTo(512L);
        assertThat(chunk.getChecksum()).isEqualTo("checksum123");
        assertThat(chunk.getStatus()).isEqualTo(ChunkStatus.RECEIVED);
        assertThat(chunk.getFilePath()).isEqualTo("/tmp/part0");
        assertThat(chunk.getCreatedBy()).isEqualTo("user");
    }

    @Test
    void receive_eachCallGeneratesUniqueId() {
        UUID uploadId = UUID.randomUUID();
        UploadChunk c1 = UploadChunk.receive(uploadId, 0, 512L, "cs1", "/p1", "u");
        UploadChunk c2 = UploadChunk.receive(uploadId, 1, 512L, "cs2", "/p2", "u");

        assertThat(c1.getId()).isNotEqualTo(c2.getId());
        assertThat(c1.getChunkIndex()).isNotEqualTo(c2.getChunkIndex());
    }

    @Test
    void reconstruct_preservesAllFields() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime uploadedAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(10);
        LocalDateTime updatedAt = LocalDateTime.now();

        UploadChunk chunk = UploadChunk.reconstruct(id, uploadId, 2, 1024L,
                "cs99", ChunkStatus.INVALID, "/stored/part2",
                uploadedAt, createdAt, updatedAt, "admin", "system");

        assertThat(chunk.getId()).isEqualTo(id);
        assertThat(chunk.getUploadId()).isEqualTo(uploadId);
        assertThat(chunk.getChunkIndex()).isEqualTo(2);
        assertThat(chunk.getSize()).isEqualTo(1024L);
        assertThat(chunk.getChecksum()).isEqualTo("cs99");
        assertThat(chunk.getStatus()).isEqualTo(ChunkStatus.INVALID);
        assertThat(chunk.getFilePath()).isEqualTo("/stored/part2");
        assertThat(chunk.getUploadedAt()).isEqualTo(uploadedAt);
        assertThat(chunk.getCreatedAt()).isEqualTo(createdAt);
        assertThat(chunk.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(chunk.getCreatedBy()).isEqualTo("admin");
        assertThat(chunk.getUpdatedBy()).isEqualTo("system");
    }

    @Test
    void chunkStatusEnumValues() {
        assertThat(ChunkStatus.values())
                .contains(ChunkStatus.PENDING, ChunkStatus.RECEIVED, ChunkStatus.INVALID);
    }
}
