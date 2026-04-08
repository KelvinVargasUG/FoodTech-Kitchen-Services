package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class GetActiveProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetActiveProductsUseCase useCase;

    @Test
    @DisplayName("execute() returns only ACTIVE products from repository (CA2)")
    void execute_returnsActiveProducts() {
        Product p1 = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
        Product p2 = Product.create("Soda", ProductType.DRINK, "Beverages", 5);

        when(productRepository.findAllActive()).thenReturn(List.of(p1, p2));

        List<Product> result = useCase.execute();

        assertEquals(2, result.size());
        verify(productRepository).findAllActive();
    }

    @Test
    @DisplayName("execute() returns empty list when no active products exist")
    void execute_noActiveProducts_returnsEmptyList() {
        when(productRepository.findAllActive()).thenReturn(List.of());

        List<Product> result = useCase.execute();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("executeByCategory() returns only products matching the given category (CA-04-03)")
    void executeByCategory_returnsOnlyMatchingCategory() {
        Product pizza  = Product.create("Pizza",  ProductType.HOT_DISH, "Principales", 1200);
        Product.create("Mojito", ProductType.DRINK,    "Bebidas",    800);

        when(productRepository.findAllActiveByCategory("Principales")).thenReturn(List.of(pizza));

        List<Product> result = useCase.executeByCategory("Principales");

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
        verify(productRepository).findAllActiveByCategory("Principales");
        verify(productRepository, never()).findAllActive();
    }

    @Test
    @DisplayName("executeByCategory() returns empty list when no products match the given category (CA-04-03)")
    void executeByCategory_noMatchingProducts_returnsEmptyList() {
        when(productRepository.findAllActiveByCategory("Postres")).thenReturn(List.of());

        List<Product> result = useCase.executeByCategory("Postres");

        assertTrue(result.isEmpty());
        verify(productRepository).findAllActiveByCategory("Postres");
    }

    @Test
    @DisplayName("executeByName() returns products whose name contains the search term (case-insensitive)")
    void executeByName_returnsMatchingProducts() {
        Product pizza = Product.create("Pizza Margherita", ProductType.HOT_DISH, "Principales", 1200);

        when(productRepository.findAllActiveByNameContaining("pizza")).thenReturn(List.of(pizza));

        List<Product> result = useCase.executeByName("pizza");

        assertEquals(1, result.size());
        assertEquals("Pizza Margherita", result.get(0).getName());
        verify(productRepository).findAllActiveByNameContaining("pizza");
    }

    @Test
    @DisplayName("executeByName() returns empty list when no products match the name")
    void executeByName_noMatch_returnsEmptyList() {
        when(productRepository.findAllActiveByNameContaining("sushi")).thenReturn(List.of());

        List<Product> result = useCase.executeByName("sushi");

        assertTrue(result.isEmpty());
        verify(productRepository).findAllActiveByNameContaining("sushi");
    }
}
