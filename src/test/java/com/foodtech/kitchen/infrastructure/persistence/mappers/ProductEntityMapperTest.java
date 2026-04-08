package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskProductEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ProductEntityMapperTest {

    private final ProductEntityMapper mapper = new ProductEntityMapper();

    private Product sampleProduct() {
        return Product.reconstruct(UUID.randomUUID(), "Lomo Saltado",
                ProductType.HOT_DISH, "Platos", 1500, ProductStatus.ACTIVE, "Peruvian dish");
    }

    @Test
    void toProductEntity_withId_setsAllFields() {
        Product product = sampleProduct();
        ProductEntity entity = mapper.toProductEntity(product);

        assertThat(entity.getUuid()).isEqualTo(product.getId().toString());
        assertThat(entity.getName()).isEqualTo("Lomo Saltado");
        assertThat(entity.getType()).isEqualTo(ProductType.HOT_DISH);
        assertThat(entity.getPrice()).isEqualTo(1500);
        assertThat(entity.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(entity.getCategory()).isEqualTo("Platos");
        assertThat(entity.getDescription()).isEqualTo("Peruvian dish");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toProductEntity_withNullId_setsNullUuid() {
        Product product = new Product("Soda", ProductType.DRINK, 300);
        ProductEntity entity = mapper.toProductEntity(product);

        assertThat(entity.getUuid()).isNull();
        assertThat(entity.getName()).isEqualTo("Soda");
    }

    @Test
    void toProductEntity_withJpaId_preservesJpaId() {
        Product product = sampleProduct();
        ProductEntity entity = mapper.toProductEntity(product, 99L);

        assertThat(entity.getId()).isEqualTo(99L);
        assertThat(entity.getUuid()).isEqualTo(product.getId().toString());
        assertThat(entity.getName()).isEqualTo("Lomo Saltado");
    }

    @Test
    void toProductEntity_withJpaId_withNullProductId_setsNullUuid() {
        Product product = new Product("Tea", ProductType.DRINK, 100);
        ProductEntity entity = mapper.toProductEntity(product, 5L);

        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getUuid()).isNull();
    }

    @Test
    void toTaskProductEntity_setsNameAndType() {
        Product product = sampleProduct();
        TaskProductEntity entity = mapper.toTaskProductEntity(product);

        assertThat(entity.getName()).isEqualTo("Lomo Saltado");
        assertThat(entity.getType()).isEqualTo(ProductType.HOT_DISH);
    }

    @Test
    void toOrderProductEntity_setsNameTypeAndPrice() {
        Product product = sampleProduct();
        OrderProductEntity entity = mapper.toOrderProductEntity(product);

        assertThat(entity.getName()).isEqualTo("Lomo Saltado");
        assertThat(entity.getType()).isEqualTo(ProductType.HOT_DISH);
        assertThat(entity.getPrice()).isEqualTo(1500);
    }

    @Test
    void toDomain_fromOrderProductEntity_createsProduct() {
        OrderProductEntity entity = OrderProductEntity.builder()
                .name("Ceviche").type(ProductType.COLD_DISH).price(2000).build();
        Product product = mapper.toDomain(entity);

        assertThat(product.getName()).isEqualTo("Ceviche");
        assertThat(product.getType()).isEqualTo(ProductType.COLD_DISH);
        assertThat(product.getPrice()).isEqualTo(2000);
    }

    @Test
    void toDomain_fromProductEntity_fullData_reconstructs() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = ProductEntity.builder()
                .uuid(id.toString()).name("Burger")
                .type(ProductType.HOT_DISH).category("Burgers")
                .price(1800).status(ProductStatus.ACTIVE)
                .description("Juicy burger").build();

        Product product = mapper.toDomain(entity);

        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo("Burger");
        assertThat(product.getCategory()).isEqualTo("Burgers");
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void toDomain_fromProductEntity_withNullUuid_createsSimpleProduct() {
        ProductEntity entity = ProductEntity.builder()
                .uuid(null).name("Water")
                .type(ProductType.DRINK).price(100).build();

        Product product = mapper.toDomain(entity);

        assertThat(product.getName()).isEqualTo("Water");
        assertThat(product.getPrice()).isEqualTo(100);
    }

    @Test
    void toDomain_fromProductEntity_withNullStatus_createsSimpleProduct() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = ProductEntity.builder()
                .uuid(id.toString()).name("Juice")
                .type(ProductType.DRINK).price(600)
                .status(null).category("Drinks").build();

        Product product = mapper.toDomain(entity);

        assertThat(product.getName()).isEqualTo("Juice");
    }

    @Test
    void toDomain_fromTaskProductEntity_createsProductWithZeroPrice() {
        TaskProductEntity entity = TaskProductEntity.builder()
                .name("Tea").type(ProductType.DRINK).build();

        Product product = mapper.toDomain(entity);

        assertThat(product.getName()).isEqualTo("Tea");
        assertThat(product.getType()).isEqualTo(ProductType.DRINK);
        assertThat(product.getPrice()).isZero();
    }
}
