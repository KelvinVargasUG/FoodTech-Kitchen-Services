package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileJpaRepository extends JpaRepository<UploadedFileEntity, Long> {
    Optional<UploadedFileEntity> findByUploadId(String uploadId);
}
