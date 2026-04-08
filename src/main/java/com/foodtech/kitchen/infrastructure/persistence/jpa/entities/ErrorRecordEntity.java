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
@Table(name = "error_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "error_record_seq")
    @SequenceGenerator(name = "error_record_seq", sequenceName = "error_record_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "upload_id", nullable = false)
    private String uploadId;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
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
