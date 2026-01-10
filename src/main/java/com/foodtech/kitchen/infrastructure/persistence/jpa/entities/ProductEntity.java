// ProductEntity.java - NUEVA entidad
package com.foodtech.kitchen.infrastructure.persistence.jpa.entities;

import com.foodtech.kitchen.domain.model.ProductType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;


    @Column(name = "order_id", insertable = false, updatable = false)
    private Long orderId;
}