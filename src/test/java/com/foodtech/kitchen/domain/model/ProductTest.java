package com.foodtech.kitchen.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
class ProductTest {

    @Test
    @DisplayName("create() should set status ACTIVE by default")
    void create_setsStatusActive() {
        Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    @DisplayName("create() should generate a non-null UUID id")
    void create_generatesId() {
        Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        assertNotNull(product.getId());
    }

    @Test
    @DisplayName("create() should set category")
    void create_setsCategory() {
        Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        assertEquals("Italian", product.getCategory());
    }

    @Test
    @DisplayName("create() should throw when name is null")
    void create_nullName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create(null, ProductType.HOT_DISH, "Italian", 10));
    }

    @Test
    @DisplayName("create() should throw when name is blank")
    void create_blankName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("  ", ProductType.HOT_DISH, "Italian", 10));
    }

    @Test
    @DisplayName("create() should throw when price is zero")
    void create_zeroPrice_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Pizza", ProductType.HOT_DISH, "Italian", 0));
    }

    @Test
    @DisplayName("create() should throw when price is negative")
    void create_negativePrice_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Pizza", ProductType.HOT_DISH, "Italian", -5));
    }

    @Test
    @DisplayName("create() should throw when category is null")
    void create_nullCategory_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Pizza", ProductType.HOT_DISH, null, 10));
    }

    @Test
    @DisplayName("create() should throw when category is blank")
    void create_blankCategory_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Pizza", ProductType.HOT_DISH, "  ", 10));
    }

    @Test
    @DisplayName("create() should throw when productType is null")
    void create_nullProductType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Pizza", null, "Italian", 10));
    }

    @Test
    @DisplayName("ProductType.DRINK should map to Station.BAR")
    void productType_drink_mapsToBar() {
        assertEquals(Station.BAR, ProductType.DRINK.getStation());
    }

    @Test
    @DisplayName("ProductType.HOT_DISH should map to Station.HOT_KITCHEN")
    void productType_hotDish_mapsToHotKitchen() {
        assertEquals(Station.HOT_KITCHEN, ProductType.HOT_DISH.getStation());
    }

    @Test
    @DisplayName("ProductType.COLD_DISH should map to Station.COLD_KITCHEN")
    void productType_coldDish_mapsToColdKitchen() {
        assertEquals(Station.COLD_KITCHEN, ProductType.COLD_DISH.getStation());
    }

    @Test
    @DisplayName("3-arg constructor should still work for backward compatibility")
    void threeArgConstructor_backwardCompat() {
        Product product = new Product("Tea", ProductType.DRINK, 5);

        assertEquals("Tea", product.getName());
        assertEquals(ProductType.DRINK, product.getType());
        assertEquals(5, product.getPrice());
    }

    @Test
    @DisplayName("deactivate() should return a new Product with status INACTIVE")
    void deactivate_setsStatusInactive() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        Product inactive = active.deactivate();

        assertEquals(ProductStatus.INACTIVE, inactive.getStatus());
        assertEquals(active.getName(), inactive.getName());
        assertEquals(active.getId(), inactive.getId());
    }

    @Test
    @DisplayName("deactivate() on already INACTIVE product should throw IllegalStateException")
    void deactivate_alreadyInactive_throwsIllegalState() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product inactive = active.deactivate();

        assertThrows(IllegalStateException.class, inactive::deactivate);
    }

    @Test
    @DisplayName("activate() should return a new Product with status ACTIVE")
    void activate_setsStatusActive() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product inactive = active.deactivate();

        Product reactivated = inactive.activate();

        assertEquals(ProductStatus.ACTIVE, reactivated.getStatus());
        assertEquals(active.getName(), reactivated.getName());
        assertEquals(active.getId(), reactivated.getId());
    }

    @Test
    @DisplayName("activate() on already ACTIVE product should throw IllegalStateException")
    void activate_alreadyActive_throwsIllegalState() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        assertThrows(IllegalStateException.class, active::activate);
    }

    @Test
    @DisplayName("create() with description should preserve description")
    void create_withDescription_preservesDescription() {
        Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10, "Wood-fired");
        assertEquals("Wood-fired", product.getDescription());
    }

    @Test
    @DisplayName("create() without description should default to null")
    void create_withoutDescription_defaultsToNull() {
        Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertNull(product.getDescription());
    }

    @Test
    @DisplayName("reconstruct() with description should preserve description")
    void reconstruct_withDescription_preservesDescription() {
        java.util.UUID id = java.util.UUID.randomUUID();
        Product product = Product.reconstruct(id,
                "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE, "Wood-fired");
        assertEquals("Wood-fired", product.getDescription());
    }

    @Test
    @DisplayName("reconstruct() without description should default to null")
    void reconstruct_withoutDescription_defaultsToNull() {
        java.util.UUID id = java.util.UUID.randomUUID();
        Product product = Product.reconstruct(id, "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        assertNull(product.getDescription());
    }

    @Test
    @DisplayName("update() should return new product with updated fields")
    void update_returnsNewProductWithUpdatedFields() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product updated = original.update("Pasta",
                "Creamy pasta", ProductType.HOT_DISH, "Italian", 15, ProductStatus.ACTIVE);

        assertEquals("Pasta", updated.getName());
        assertEquals("Creamy pasta", updated.getDescription());
        assertEquals(15, updated.getPrice());
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    @DisplayName("update() should preserve id from original product")
    void update_preservesOriginalId() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product updated = original.update("Pasta", null, ProductType.COLD_DISH, "Italian", 15, ProductStatus.INACTIVE);
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    @DisplayName("update() with null description should be allowed")
    void update_nullDescription_shouldBeAllowed() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10, "Wood-fired");
        Product updated = original.update("Pizza", null, ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        assertNull(updated.getDescription());
    }

    @Test
    @DisplayName("update() should throw when name is blank")
    void update_blankName_throwsIllegalArgument() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertThrows(IllegalArgumentException.class,
                () -> original.update("  ", null, ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE));
    }

    @Test
    @DisplayName("update() should throw when type is null")
    void update_nullType_throwsIllegalArgument() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertThrows(IllegalArgumentException.class,
                () -> original.update("Pizza", null, null, "Italian", 10, ProductStatus.ACTIVE));
    }

    @Test
    @DisplayName("update() should throw when status is null")
    void update_nullStatus_throwsIllegalArgument() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertThrows(IllegalArgumentException.class,
                () -> original.update("Pizza", null, ProductType.HOT_DISH, "Italian", 10, null));
    }

    @Test
    @DisplayName("update() should throw when price is zero")
    void update_zeroPrice_throwsIllegalArgument() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertThrows(IllegalArgumentException.class,
                () -> original.update("Pizza", null, ProductType.HOT_DISH, "Italian", 0, ProductStatus.ACTIVE));
    }

    @Test
    @DisplayName("update() should throw when category is blank")
    void update_blankCategory_throwsIllegalArgument() {
        Product original = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        assertThrows(IllegalArgumentException.class,
                () -> original.update("Pizza", null, ProductType.HOT_DISH, "  ", 10, ProductStatus.ACTIVE));
    }

    @Test
    @DisplayName("deactivate() should preserve description")
    void deactivate_preservesDescription() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10, "Wood-fired");
        Product inactive = active.deactivate();
        assertEquals("Wood-fired", inactive.getDescription());
    }

    @Test
    @DisplayName("activate() should preserve description")
    void activate_preservesDescription() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10, "Wood-fired");
        Product inactive = active.deactivate();
        Product reactivated = inactive.activate();
        assertEquals("Wood-fired", reactivated.getDescription());
    }
}
