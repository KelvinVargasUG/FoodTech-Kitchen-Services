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
@Table(name = "uploaded_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uploaded_file_seq")
    @SequenceGenerator(name = "uploaded_file_seq", sequenceName = "uploaded_file_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "upload_id", nullable = false, unique = true)
    private String uploadId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private String checksum;

    @Column(name = "assembled_at")
    private LocalDateTime assembledAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
