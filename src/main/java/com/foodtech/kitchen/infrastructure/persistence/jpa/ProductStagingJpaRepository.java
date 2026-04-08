package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductStagingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStagingJpaRepository extends JpaRepository<ProductStagingEntity, Long> {
    List<ProductStagingEntity> findByUploadId(String uploadId);
}
