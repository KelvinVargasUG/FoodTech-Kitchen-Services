package com.foodtech.kitchen.domain.model.upload;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductStaging {

    private final UUID id;
    private final UUID uploadId;
    private final String name;
    private final String price;
    private final String category;
    private final String station;
    private final String description;
    private final String status;
    private final String errorMessage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private ProductStaging(UUID id, UUID uploadId, String name, String price,
                           String category, String station, String description, String status,
                           String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String createdBy, String updatedBy) {
        this.id = id;
        this.uploadId = uploadId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.station = station;
        this.description = description;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static ProductStaging fromRawRow(UUID uploadId, String name, String price,
                                            String category, String station, String description,
                                            String status, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new ProductStaging(UUID.randomUUID(), uploadId, name, price,
                category, station, description, status,
                null, now, now, createdBy, createdBy);
    }

    public static ProductStaging reconstruct(UUID id, UUID uploadId, String name, String price,
                                             String category, String station, String description,
                                             String status, String errorMessage,
                                             LocalDateTime createdAt, LocalDateTime updatedAt,
                                             String createdBy, String updatedBy) {
        return new ProductStaging(id, uploadId, name, price, category, station,
                description, status, errorMessage, createdAt, updatedAt, createdBy, updatedBy);
    }

    public boolean hasError() { return errorMessage != null && !errorMessage.isBlank(); }

    public UUID getId() { return id; }
    public UUID getUploadId() { return uploadId; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getStation() { return station; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
}
