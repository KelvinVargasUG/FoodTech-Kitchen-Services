package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.persistence.jpa.ProductJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.ProductEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @Mock
    private ProductEntityMapper mapper;

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    @Test
    @DisplayName("save() inserts a new product when its uuid is not found in DB (new product path)")
    void save_newProduct_uuidNotFoundInDb_executesInsert() {
        Product product = Product.create("Limonada", ProductType.DRINK, "Bebidas", 500);
        ProductEntity entity = buildEntity(null, product.getId().toString());
        ProductEntity saved = buildEntity(1L, product.getId().toString());
        Product expected = Product.create("Limonada", ProductType.DRINK, "Bebidas", 500);

        when(jpaRepository.findByUuid(product.getId().toString())).thenReturn(Optional.empty());
        when(mapper.toProductEntity(product)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(expected);

        Product result = adapter.save(product);

        assertNotNull(result);
        verify(mapper).toProductEntity(product);
        verify(jpaRepository).save(entity);
        verify(jpaRepository).findByUuid(product.getId().toString());
    }

    @Test
    @DisplayName("save() issues UPDATE using jpa Long id when uuid matches an existing entity")
    void save_existingProduct_uuidFound_executesUpdate() {
        UUID uuid = UUID.randomUUID();
        Product product = Product.reconstruct(uuid,
                "Lomo Saltado", ProductType.HOT_DISH, "Platos", 1500, ProductStatus.ACTIVE);
        ProductEntity existing = buildEntity(42L, uuid.toString());
        ProductEntity updatedEntity = buildEntity(42L, uuid.toString());
        Product expected = Product.reconstruct(uuid,
                "Lomo Saltado", ProductType.HOT_DISH, "Platos", 1500, ProductStatus.ACTIVE);

        when(jpaRepository.findByUuid(uuid.toString())).thenReturn(Optional.of(existing));
        when(mapper.toProductEntity(product, 42L)).thenReturn(updatedEntity);
        when(jpaRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(mapper.toDomain(updatedEntity)).thenReturn(expected);

        Product result = adapter.save(product);

        assertNotNull(result);
        verify(mapper).toProductEntity(product, 42L);
        verify(jpaRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("save() falls back to INSERT when product uuid (from reconstruct) is not found in DB")
    void save_reconstructedProduct_notFoundInDb_fallsBackToInsert() {
        UUID uuid = UUID.randomUUID();
        Product product = Product.reconstruct(uuid,
                "Ceviche", ProductType.COLD_DISH, "Platos", 2000, ProductStatus.ACTIVE);
        ProductEntity entity = buildEntity(null, uuid.toString());
        ProductEntity saved = buildEntity(5L, uuid.toString());
        Product expected = Product.reconstruct(uuid,
                "Ceviche", ProductType.COLD_DISH, "Platos", 2000, ProductStatus.ACTIVE);

        when(jpaRepository.findByUuid(uuid.toString())).thenReturn(Optional.empty());
        when(mapper.toProductEntity(product)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(expected);

        Product result = adapter.save(product);

        assertNotNull(result);
        verify(mapper).toProductEntity(product);
        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("existsByName() returns true when a product with that name already exists")
    void existsByName_existingName_returnsTrue() {
        when(jpaRepository.existsByName("Lomo Saltado")).thenReturn(true);
        assertTrue(adapter.existsByName("Lomo Saltado"));
        verify(jpaRepository).existsByName("Lomo Saltado");
    }

    @Test
    @DisplayName("existsByName() returns false when no product with that name exists")
    void existsByName_unknownName_returnsFalse() {
        when(jpaRepository.existsByName("Inexistente")).thenReturn(false);
        assertFalse(adapter.existsByName("Inexistente"));
    }

    @Test
    @DisplayName("findAllActive() returns mapped domain list of all active products")
    void findAllActive_returnsMappedDomainList() {
        ProductEntity e1 = buildEntity(1L, UUID.randomUUID().toString());
        ProductEntity e2 = buildEntity(2L, UUID.randomUUID().toString());
        Product p1 = Product.create("A", ProductType.HOT_DISH, "Cat", 100);
        Product p2 = Product.create("B", ProductType.DRINK, "Cat", 200);

        when(jpaRepository.findAllByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(p1);
        when(mapper.toDomain(e2)).thenReturn(p2);

        List<Product> result = adapter.findAllActive();

        assertEquals(2, result.size());
        verify(jpaRepository).findAllByStatus(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("findAllActive() returns empty list when no active products exist")
    void findAllActive_noActiveProducts_returnsEmptyList() {
        when(jpaRepository.findAllByStatus(ProductStatus.ACTIVE)).thenReturn(List.of());
        assertTrue(adapter.findAllActive().isEmpty());
    }

    @Test
    @DisplayName("findByUuid() returns mapped domain product when entity exists")
    void findByUuid_found_returnsDomainProduct() {
        UUID uuid = UUID.randomUUID();
        ProductEntity entity = buildEntity(3L, uuid.toString());
        Product product = Product.create("X", ProductType.HOT_DISH, "Cat", 300);

        when(jpaRepository.findByUuid(uuid.toString())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);

        Optional<Product> result = adapter.findByUuid(uuid);

        assertTrue(result.isPresent());
        verify(jpaRepository).findByUuid(uuid.toString());
    }

    @Test
    @DisplayName("findByUuid() returns empty Optional when entity does not exist")
    void findByUuid_notFound_returnsEmpty() {
        UUID uuid = UUID.randomUUID();
        when(jpaRepository.findByUuid(uuid.toString())).thenReturn(Optional.empty());

        assertTrue(adapter.findByUuid(uuid).isEmpty());
    }

    @Test
    @DisplayName("findByName() returns mapped domain product when entity exists")
    void findByName_found_returnsDomainProduct() {
        ProductEntity entity = buildEntity(4L, UUID.randomUUID().toString());
        Product product = Product.create("Pasta", ProductType.HOT_DISH, "Cat", 1200);

        when(jpaRepository.findByName("Pasta")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);

        assertTrue(adapter.findByName("Pasta").isPresent());
    }

    @Test
    @DisplayName("findByName() returns empty Optional when no product matches")
    void findByName_notFound_returnsEmpty() {
        when(jpaRepository.findByName("Ghost")).thenReturn(Optional.empty());
        assertTrue(adapter.findByName("Ghost").isEmpty());
    }

    @Test
    @DisplayName("updateStatus() delegates to jpaRepository.updateStatusByUuid with uuid string and status")
    void updateStatus_delegatesToJpa() {
        UUID uuid = UUID.randomUUID();
        adapter.updateStatus(uuid, ProductStatus.INACTIVE);
        verify(jpaRepository).updateStatusByUuid(uuid.toString(), ProductStatus.INACTIVE);
    }

    @Test
    @DisplayName("updateStatus() works correctly for ACTIVE status too")
    void updateStatus_activeStatus_delegatesToJpa() {
        UUID uuid = UUID.randomUUID();
        adapter.updateStatus(uuid, ProductStatus.ACTIVE);
        verify(jpaRepository).updateStatusByUuid(uuid.toString(), ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("findAllActiveByCategory() returns mapped domain list for matching category (CA-04-03)")
    void findAllActiveByCategory_returnsFilteredProducts() {
        ProductEntity entity = buildEntity(1L, UUID.randomUUID().toString());
        Product expected = Product.create("Pizza", ProductType.HOT_DISH, "Principales", 1200);

        when(jpaRepository.findAllByStatusAndCategory(ProductStatus.ACTIVE, "Principales"))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        List<Product> result = adapter.findAllActiveByCategory("Principales");

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
        verify(jpaRepository).findAllByStatusAndCategory(ProductStatus.ACTIVE, "Principales");
    }

    @Test
    @DisplayName("findAllActiveByCategory() returns empty list when no products match category (CA-04-03)")
    void findAllActiveByCategory_returnsEmptyWhenNone() {
        when(jpaRepository.findAllByStatusAndCategory(ProductStatus.ACTIVE, "Postres"))
                .thenReturn(List.of());

        List<Product> result = adapter.findAllActiveByCategory("Postres");

        assertTrue(result.isEmpty());
        verify(jpaRepository).findAllByStatusAndCategory(ProductStatus.ACTIVE, "Postres");
    }

    @Test
    @DisplayName("findAllActiveByNameContaining() returns products whose name matches (case-insensitive)")
    void findAllActiveByNameContaining_returnsMatchingProducts() {
        ProductEntity entity = buildEntity(1L, UUID.randomUUID().toString());
        Product expected = Product.create("Pizza Margherita", ProductType.HOT_DISH, "Principales", 1200);

        when(jpaRepository.findAllByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, "pizza"))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        List<Product> result = adapter.findAllActiveByNameContaining("pizza");

        assertEquals(1, result.size());
        assertEquals("Pizza Margherita", result.get(0).getName());
        verify(jpaRepository).findAllByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, "pizza");
    }

    @Test
    @DisplayName("findAllActiveByNameContaining() returns empty list when no match")
    void findAllActiveByNameContaining_returnsEmptyWhenNone() {
        when(jpaRepository.findAllByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, "sushi"))
                .thenReturn(List.of());

        List<Product> result = adapter.findAllActiveByNameContaining("sushi");

        assertTrue(result.isEmpty());
        verify(jpaRepository).findAllByStatusAndNameContainingIgnoreCase(ProductStatus.ACTIVE, "sushi");
    }

    private ProductEntity buildEntity(Long id, String uuid) {
        return ProductEntity.builder()
                .id(id)
                .uuid(uuid)
                .name("Test")
                .type(ProductType.HOT_DISH)
                .price(100)
                .status(ProductStatus.ACTIVE)
                .category("Cat")
                .build();
    }
}
