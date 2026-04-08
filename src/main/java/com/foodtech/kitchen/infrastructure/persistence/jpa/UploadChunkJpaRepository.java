package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadChunkJpaRepository extends JpaRepository<UploadChunkEntity, Long> {
    List<UploadChunkEntity> findByUploadIdOrderByChunkIndex(String uploadId);
}
