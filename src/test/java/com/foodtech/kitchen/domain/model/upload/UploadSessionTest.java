package com.foodtech.kitchen.domain.model.upload;

import com.foodtech.kitchen.domain.model.upload.UploadSession.ProcessingStatus;
import com.foodtech.kitchen.domain.model.upload.UploadSession.UploadStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Tag;

@Tag("unit")
class UploadSessionTest {

    @Test
    void initiate_setsDefaultStatusAndReturnsDomain() {
        UploadSession session = UploadSession.initiate("products.csv", "admin");

        assertThat(session.getId()).isNotNull();
        assertThat(session.getFileName()).isEqualTo("products.csv");
        assertThat(session.getUploadStatus()).isEqualTo(UploadStatus.UPLOADING);
        assertThat(session.getProcessingStatus()).isEqualTo(ProcessingStatus.PENDING);
        assertThat(session.getTotalRecords()).isZero();
        assertThat(session.getProcessedRecords()).isZero();
        assertThat(session.getFailedRecords()).isZero();
        assertThat(session.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    void initiate_throwsOnNullFileName() {
        assertThatThrownBy(() -> UploadSession.initiate(null, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fileName");
    }

    @Test
    void initiate_throwsOnBlankFileName() {
        assertThatThrownBy(() -> UploadSession.initiate("   ", "admin"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reconstruct_preservesAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UploadSession session = UploadSession.reconstruct(
                id, "data.csv", UploadStatus.UPLOADED, ProcessingStatus.COMPLETED,
                100, 95, 5, now, now, "admin", "admin");

        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getFileName()).isEqualTo("data.csv");
        assertThat(session.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
        assertThat(session.getProcessingStatus()).isEqualTo(ProcessingStatus.COMPLETED);
        assertThat(session.getTotalRecords()).isEqualTo(100);
        assertThat(session.getProcessedRecords()).isEqualTo(95);
        assertThat(session.getFailedRecords()).isEqualTo(5);
    }

    @Test
    void withUploadStatus_returnsNewInstanceWithUpdatedStatus() {
        UploadSession original = UploadSession.initiate("file.csv", "user");
        UploadSession updated = original.withUploadStatus(UploadStatus.UPLOADED);

        assertThat(updated.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
        assertThat(original.getUploadStatus()).isEqualTo(UploadStatus.UPLOADING);
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getFileName()).isEqualTo(original.getFileName());
    }

    @Test
    void withUploadStatus_failedStatus() {
        UploadSession session = UploadSession.initiate("file.csv", "user");
        UploadSession failed = session.withUploadStatus(UploadStatus.FAILED);

        assertThat(failed.getUploadStatus()).isEqualTo(UploadStatus.FAILED);
    }

    @Test
    void withProcessingResult_returnsNewInstanceWithCounts() {
        UploadSession session = UploadSession.initiate("file.csv", "user");
        UploadSession result = session.withProcessingResult(ProcessingStatus.COMPLETED, 50, 48, 2);

        assertThat(result.getProcessingStatus()).isEqualTo(ProcessingStatus.COMPLETED);
        assertThat(result.getTotalRecords()).isEqualTo(50);
        assertThat(result.getProcessedRecords()).isEqualTo(48);
        assertThat(result.getFailedRecords()).isEqualTo(2);
        assertThat(result.getId()).isEqualTo(session.getId());
    }

    @Test
    void withProcessingResult_failedStatus() {
        UploadSession session = UploadSession.initiate("file.csv", "user");
        UploadSession result = session.withProcessingResult(ProcessingStatus.FAILED, 10, 3, 7);

        assertThat(result.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILED);
        assertThat(result.getFailedRecords()).isEqualTo(7);
    }

    @Test
    void initiate_eachCallGeneratesUniqueId() {
        UploadSession s1 = UploadSession.initiate("a.csv", "u");
        UploadSession s2 = UploadSession.initiate("b.csv", "u");

        assertThat(s1.getId()).isNotEqualTo(s2.getId());
    }
}
