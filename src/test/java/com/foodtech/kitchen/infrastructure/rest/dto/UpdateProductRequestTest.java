package com.foodtech.kitchen.infrastructure.rest.dto;

import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class UpdateProductRequestTest {

    @Test
    void allArgsConstructor_setsAllFields() {
        UpdateProductRequest req = new UpdateProductRequest(
                "Lomo", "Delicious", ProductType.HOT_DISH, "Platos", 1500, ProductStatus.ACTIVE);

        assertThat(req.getName()).isEqualTo("Lomo");
        assertThat(req.getDescription()).isEqualTo("Delicious");
        assertThat(req.getType()).isEqualTo(ProductType.HOT_DISH);
        assertThat(req.getCategory()).isEqualTo("Platos");
        assertThat(req.getPrice()).isEqualTo(1500);
        assertThat(req.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void noArgsConstructor_fieldsAreNull() {
        UpdateProductRequest req = new UpdateProductRequest();

        assertThat(req.getName()).isNull();
        assertThat(req.getDescription()).isNull();
        assertThat(req.getType()).isNull();
        assertThat(req.getCategory()).isNull();
        assertThat(req.getPrice()).isZero();
        assertThat(req.getStatus()).isNull();
    }

    @Test
    void gettersReturnCorrectValues_withInactiveStatus() {
        UpdateProductRequest req = new UpdateProductRequest(
                "Ceviche", null, ProductType.COLD_DISH, "Entradas", 2000, ProductStatus.INACTIVE);

        assertThat(req.getName()).isEqualTo("Ceviche");
        assertThat(req.getDescription()).isNull();
        assertThat(req.getType()).isEqualTo(ProductType.COLD_DISH);
        assertThat(req.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }
}
