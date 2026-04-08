package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UploadSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadSessionJpaRepository extends JpaRepository<UploadSessionEntity, Long> {
    Optional<UploadSessionEntity> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
}
