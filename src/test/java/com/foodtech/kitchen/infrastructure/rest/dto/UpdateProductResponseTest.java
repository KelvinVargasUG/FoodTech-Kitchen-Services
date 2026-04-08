package com.foodtech.kitchen.infrastructure.rest.dto;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class UpdateProductResponseTest {

    @Test
    void constructor_setsAllFields() {
        UUID id = UUID.randomUUID();
        UpdateProductResponse resp = new UpdateProductResponse(
                id, "Pasta", "Italian pasta", ProductType.HOT_DISH, "Pastas", 1200, ProductStatus.ACTIVE);

        assertThat(resp.getId()).isEqualTo(id);
        assertThat(resp.getName()).isEqualTo("Pasta");
        assertThat(resp.getDescription()).isEqualTo("Italian pasta");
        assertThat(resp.getType()).isEqualTo(ProductType.HOT_DISH);
        assertThat(resp.getCategory()).isEqualTo("Pastas");
        assertThat(resp.getPrice()).isEqualTo(1200);
        assertThat(resp.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void constructor_withNullDescription_setsNullDescription() {
        UUID id = UUID.randomUUID();
        UpdateProductResponse resp = new UpdateProductResponse(
                id, "Agua", null, ProductType.DRINK, "Bebidas", 300, ProductStatus.ACTIVE);

        assertThat(resp.getId()).isEqualTo(id);
        assertThat(resp.getName()).isEqualTo("Agua");
        assertThat(resp.getDescription()).isNull();
        assertThat(resp.getType()).isEqualTo(ProductType.DRINK);
        assertThat(resp.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void getters_returnCorrectValuesForInactiveProduct() {
        UUID id = UUID.randomUUID();
        UpdateProductResponse resp = new UpdateProductResponse(
                id, "Ceviche", "Fresh ceviche", ProductType.COLD_DISH, "Entradas", 2500, ProductStatus.INACTIVE);

        assertThat(resp.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(resp.getType()).isEqualTo(ProductType.COLD_DISH);
        assertThat(resp.getPrice()).isEqualTo(2500);
    }
}
