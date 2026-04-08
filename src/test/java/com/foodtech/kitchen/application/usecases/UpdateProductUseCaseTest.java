package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.ProductNotFoundException;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.usecases.dto.UpdateProductCommand;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductUseCase useCase;

    @Test
    @DisplayName("execute() happy path — product found, updated and saved")
    void execute_happyPath_productUpdatedAndSaved() {
        UUID uuid = UUID.randomUUID();
        Product existing = Product.reconstruct(uuid,
                "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        UpdateProductCommand command = new UpdateProductCommand("Pasta",
                "Creamy", ProductType.HOT_DISH, "Italian", 15, ProductStatus.ACTIVE);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = useCase.execute(uuid, command);

        assertNotNull(result);
        assertEquals("Pasta", result.getName());
        assertEquals("Creamy", result.getDescription());
        assertEquals(15, result.getPrice());
        assertEquals(uuid, result.getId());
        verify(productRepository).findByUuid(uuid);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("execute() should throw ProductNotFoundException when product does not exist")
    void execute_notFound_throwsProductNotFoundException() {
        UUID uuid = UUID.randomUUID();
        UpdateProductCommand command = new UpdateProductCommand("Pasta",
                null, ProductType.HOT_DISH, "Italian", 15, ProductStatus.ACTIVE);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> useCase.execute(uuid, command));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute() should allow changing status to INACTIVE")
    void execute_changeStatusToInactive() {
        UUID uuid = UUID.randomUUID();
        Product existing = Product.reconstruct(uuid,
                "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        UpdateProductCommand command = new UpdateProductCommand("Pizza",
                null, ProductType.HOT_DISH, "Italian", 10, ProductStatus.INACTIVE);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = useCase.execute(uuid, command);

        assertEquals(ProductStatus.INACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("execute() should propagate IllegalArgumentException from domain validation (blank name)")
    void execute_blankName_throwsIllegalArgument() {
        UUID uuid = UUID.randomUUID();
        Product existing = Product.reconstruct(uuid,
                "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        UpdateProductCommand command = new UpdateProductCommand("  ",
                null, ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(uuid, command));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute() should update product with description")
    void execute_withDescription_updatesDescription() {
        UUID uuid = UUID.randomUUID();
        Product existing = Product.reconstruct(uuid,
                "Pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);
        UpdateProductCommand command = new UpdateProductCommand("Pizza",
                "Wood-fired pizza", ProductType.HOT_DISH, "Italian", 10, ProductStatus.ACTIVE);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = useCase.execute(uuid, command);

        assertEquals("Wood-fired pizza", result.getDescription());
    }
}
