package com.foodtech.kitchen.infrastructure.persistence.mappers;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderEntity;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.OrderProductEntity;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class OrderEntityMapperTest {

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private OrderEntityMapper mapper;

    @Test
    void toEntity_mapsProductsAndStatus() {

        List<Product> products = sampleProducts();
        Product product = products.get(0);
        Order order = Order.reconstruct(10L, "T1", "Cliente Test", "test@test.com", products, OrderStatus.IN_PROGRESS);
        OrderProductEntity orderProductEntity = OrderProductEntity.builder()
                .name("Soda")
                .type(ProductType.DRINK)
                .price(5)
                .build();
        when(productEntityMapper.toOrderProductEntity(product)).thenReturn(orderProductEntity);

        OrderEntity entity = mapper.toEntity(order);

        assertNotNull(entity);
        assertEquals(10L, entity.getId());
        assertEquals("T1", entity.getTableNumber());
        assertEquals(OrderStatus.IN_PROGRESS, entity.getStatus());
        assertEquals(1, entity.getProducts().size());
        verify(productEntityMapper).toOrderProductEntity(product);
    }

    @Test
    void toDomain_defaultsStatusWhenNull() {

        Product product = new Product("Soda", ProductType.DRINK, 5);
        OrderProductEntity orderProductEntity = OrderProductEntity.builder()
                .name("Soda")
                .type(ProductType.DRINK)
                .price(5)
                .build();
        when(productEntityMapper.toDomain(orderProductEntity)).thenReturn(product);

        OrderEntity entity = OrderEntity.builder()
                .id(11L)
                .tableNumber("T2")
                .customerName("Cliente Test")
                .customerEmail("test@test.com")
                .status(null)
                .products(List.of(orderProductEntity))
                .build();

        Order order = mapper.toDomain(entity);

        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(1, order.getProducts().size());
        verify(productEntityMapper).toDomain(orderProductEntity);
    }

    private List<Product> sampleProducts() {
        return List.of(new Product("Soda", ProductType.DRINK, 5));
    }
}
