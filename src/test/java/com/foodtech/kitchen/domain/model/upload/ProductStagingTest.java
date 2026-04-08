package com.foodtech.kitchen.domain.model.upload;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Tag;

@Tag("unit")
class ProductStagingTest {

    @Test
    void fromRawRow_setsAllFieldsWithNullError() {
        UUID uploadId = UUID.randomUUID();
        ProductStaging staging = ProductStaging.fromRawRow(uploadId,
                "Burger", "9.99", "FOOD", "GRILL", "Tasty burger", "VALID", "user1");

        assertThat(staging.getId()).isNotNull();
        assertThat(staging.getUploadId()).isEqualTo(uploadId);
        assertThat(staging.getName()).isEqualTo("Burger");
        assertThat(staging.getPrice()).isEqualTo("9.99");
        assertThat(staging.getCategory()).isEqualTo("FOOD");
        assertThat(staging.getStation()).isEqualTo("GRILL");
        assertThat(staging.getDescription()).isEqualTo("Tasty burger");
        assertThat(staging.getStatus()).isEqualTo("VALID");
        assertThat(staging.getErrorMessage()).isNull();
        assertThat(staging.getCreatedBy()).isEqualTo("user1");
        assertThat(staging.getUpdatedBy()).isEqualTo("user1");
    }

    @Test
    void fromRawRow_eachCallGeneratesUniqueId() {
        UUID uploadId = UUID.randomUUID();
        ProductStaging s1 = ProductStaging.fromRawRow(uploadId, "A", "1", "C", "S", "d", "V", "u");
        ProductStaging s2 = ProductStaging.fromRawRow(uploadId, "B", "2", "C", "S", "d", "V", "u");

        assertThat(s1.getId()).isNotEqualTo(s2.getId());
    }

    @Test
    void hasError_returnsFalse_whenErrorMessageIsNull() {
        UUID uploadId = UUID.randomUUID();
        ProductStaging staging = ProductStaging.fromRawRow(uploadId,
                "Burger", "9.99", "FOOD", "GRILL", "desc", "VALID", "user");

        assertThat(staging.hasError()).isFalse();
    }

    @Test
    void hasError_returnsTrue_whenErrorMessageIsPresent() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ProductStaging staging = ProductStaging.reconstruct(id, uploadId,
                "Bad", "abc", "FOOD", "GRILL", "desc", "ERROR",
                "Price is not a valid number",
                now, now, "user", "user");

        assertThat(staging.hasError()).isTrue();
    }

    @Test
    void hasError_returnsFalse_whenErrorMessageIsBlank() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ProductStaging staging = ProductStaging.reconstruct(id, uploadId,
                "Item", "5.00", "FOOD", "BAR", "desc", "VALID",
                "   ",
                now, now, "user", "user");

        assertThat(staging.hasError()).isFalse();
    }

    @Test
    void reconstruct_preservesAllFields() {
        UUID id = UUID.randomUUID();
        UUID uploadId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusMinutes(10);
        LocalDateTime updated = LocalDateTime.now();

        ProductStaging staging = ProductStaging.reconstruct(id, uploadId,
                "Pizza", "12.50", "FOOD", "OVEN", "Margherita", "VALID",
                null, created, updated, "admin", "system");

        assertThat(staging.getId()).isEqualTo(id);
        assertThat(staging.getUploadId()).isEqualTo(uploadId);
        assertThat(staging.getName()).isEqualTo("Pizza");
        assertThat(staging.getPrice()).isEqualTo("12.50");
        assertThat(staging.getCategory()).isEqualTo("FOOD");
        assertThat(staging.getStation()).isEqualTo("OVEN");
        assertThat(staging.getDescription()).isEqualTo("Margherita");
        assertThat(staging.getStatus()).isEqualTo("VALID");
        assertThat(staging.getErrorMessage()).isNull();
        assertThat(staging.getCreatedAt()).isEqualTo(created);
        assertThat(staging.getUpdatedAt()).isEqualTo(updated);
        assertThat(staging.getCreatedBy()).isEqualTo("admin");
        assertThat(staging.getUpdatedBy()).isEqualTo("system");
    }
}
