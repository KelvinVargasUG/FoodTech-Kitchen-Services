package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.TaskStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByStation(Station station);
    List<TaskEntity> findByStationAndStatus(Station station, TaskStatus status);
    List<TaskEntity> findByOrderId(Long orderId);

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH t.products WHERE t.id = :id")
    Optional<TaskEntity> findByIdWithProducts(@Param("id") Long id);
}