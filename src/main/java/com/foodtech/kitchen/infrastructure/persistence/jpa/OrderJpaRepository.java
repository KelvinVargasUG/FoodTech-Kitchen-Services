package com.foodtech.kitchen.infrastructure.persistence.jpa;

import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    
}
