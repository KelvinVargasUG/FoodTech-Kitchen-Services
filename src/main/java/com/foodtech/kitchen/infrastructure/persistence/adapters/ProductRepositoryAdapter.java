package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.ProductJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.ProductEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository, ProductEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        if (product.getId() != null) {
            java.util.Optional<ProductEntity> existing = jpaRepository.findByUuid(product.getId().toString());
            if (existing.isPresent()) {
                ProductEntity entity = mapper.toProductEntity(product, existing.get().getId());
                ProductEntity saved = jpaRepository.save(entity);
                return mapper.toDomain(saved);
            }
        }
        ProductEntity entity = mapper.toProductEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public List<Product> findAllActive() {
        return jpaRepository.findAllByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllActiveByCategory(String category) {
        return jpaRepository.findAllByStatusAndCategory(ProductStatus.ACTIVE, category)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllActiveByNameContaining(String name) {
        return jpaRepository.findAllByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, name)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid.toString())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public void updateStatus(UUID uuid, ProductStatus status) {
        jpaRepository.updateStatusByUuid(uuid.toString(), status);
    }
}
