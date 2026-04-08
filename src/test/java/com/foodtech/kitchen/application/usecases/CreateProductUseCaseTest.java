package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.DuplicateProductException;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.usecases.dto.CreateProductCommand;
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
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Test
    @DisplayName("execute() happy path — product created, saved and returned")
    void execute_happyPath_productCreatedSavedAndReturned() {

        CreateProductCommand command = new CreateProductCommand("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product saved = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);

        when(productRepository.existsByName("Pizza")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = useCase.execute(command);

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        assertEquals(ProductStatus.ACTIVE, result.getStatus());
        assertEquals("Italian", result.getCategory());
        verify(productRepository).existsByName("Pizza");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("execute() should throw DuplicateProductException when name already exists")
    void execute_duplicateName_throwsDuplicateProductException() {

        CreateProductCommand command = new CreateProductCommand("Pizza", ProductType.HOT_DISH, "Italian", 10);
        when(productRepository.existsByName("Pizza")).thenReturn(true);

        assertThrows(DuplicateProductException.class, () -> useCase.execute(command));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute() should throw IllegalArgumentException when price is zero")
    void execute_zeroPrice_throwsIllegalArgumentException() {

        CreateProductCommand command = new CreateProductCommand("Pizza", ProductType.HOT_DISH, "Italian", 0);
        when(productRepository.existsByName("Pizza")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute() should throw IllegalArgumentException when category is null")
    void execute_nullCategory_throwsIllegalArgumentException() {

        CreateProductCommand command = new CreateProductCommand("Pizza", ProductType.HOT_DISH, null, 10);
        when(productRepository.existsByName("Pizza")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(productRepository, never()).save(any());
    }
}
