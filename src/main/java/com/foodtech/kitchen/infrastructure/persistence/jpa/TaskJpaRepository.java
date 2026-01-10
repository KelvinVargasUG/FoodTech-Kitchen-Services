package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByStation(Station station);
}