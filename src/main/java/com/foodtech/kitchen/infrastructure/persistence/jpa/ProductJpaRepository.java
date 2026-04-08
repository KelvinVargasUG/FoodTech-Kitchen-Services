package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByName(String name);
    List<ProductEntity> findAllByStatus(ProductStatus status);
    Optional<ProductEntity> findByUuid(String uuid);
    Optional<ProductEntity> findByName(String name);

    List<ProductEntity> findAllByStatusAndCategory(ProductStatus status, String category);

    List<ProductEntity> findAllByStatusAndNameContainingIgnoreCase(ProductStatus status, String name);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.status = :status WHERE p.uuid = :uuid")
    void updateStatusByUuid(@Param("uuid") String uuid, @Param("status") ProductStatus status);
}
