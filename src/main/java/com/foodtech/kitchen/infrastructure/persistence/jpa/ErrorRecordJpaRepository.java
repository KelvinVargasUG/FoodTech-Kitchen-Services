package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ErrorRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorRecordJpaRepository extends JpaRepository<ErrorRecordEntity, Long> {
    List<ErrorRecordEntity> findByUploadIdOrderByRowNumber(String uploadId);
}
