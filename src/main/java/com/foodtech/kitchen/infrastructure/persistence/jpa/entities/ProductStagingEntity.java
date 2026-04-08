package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_staging")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStagingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_staging_seq")
    @SequenceGenerator(name = "product_staging_seq", sequenceName = "product_staging_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "upload_id", nullable = false)
    private String uploadId;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String price;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private String station;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private String status;

    @Column(name = "error_message", nullable = true, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
