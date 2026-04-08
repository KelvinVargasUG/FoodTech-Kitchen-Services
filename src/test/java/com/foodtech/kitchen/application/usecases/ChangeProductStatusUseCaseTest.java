package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.ProductNotFoundException;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ChangeProductStatusUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ChangeProductStatusUseCase useCase;

    @Test
    @DisplayName("deactivate() — ACTIVE product is saved as INACTIVE (CA1)")
    void deactivate_activeProduct_savedAsInactive() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        when(productRepository.findByUuid(active.getId())).thenReturn(Optional.of(active));
        doNothing().when(productRepository).updateStatus(any(), any());

        Product result = useCase.deactivate(active.getId());

        assertEquals(ProductStatus.INACTIVE, result.getStatus());
        verify(productRepository).updateStatus(eq(active.getId()), eq(ProductStatus.INACTIVE));
    }

    @Test
    @DisplayName("deactivate() — unknown UUID throws ProductNotFoundException")
    void deactivate_unknownId_throwsProductNotFoundException() {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> useCase.deactivate(uuid));
        verify(productRepository, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("activate() — INACTIVE product is saved as ACTIVE (CA5)")
    void activate_inactiveProduct_savedAsActive() {
        Product active = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product inactive = active.deactivate();

        when(productRepository.findByUuid(inactive.getId())).thenReturn(Optional.of(inactive));
        doNothing().when(productRepository).updateStatus(any(), any());

        Product result = useCase.activate(inactive.getId());

        assertEquals(ProductStatus.ACTIVE, result.getStatus());
        verify(productRepository).updateStatus(eq(inactive.getId()), eq(ProductStatus.ACTIVE));
    }

    @Test
    @DisplayName("activate() — unknown UUID throws ProductNotFoundException")
    void activate_unknownId_throwsProductNotFoundException() {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> useCase.activate(uuid));
        verify(productRepository, never()).updateStatus(any(), any());
    }
}
