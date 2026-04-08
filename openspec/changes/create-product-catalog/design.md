# Design: Product Catalog Management

## Technical Approach

Extend the existing hexagonal architecture to support product creation as a standalone aggregate. Enhance `Product.java` with identity (ID), lifecycle (status), and category fields while preserving backward compatibility with order processing. Follow established patterns: `OrderRepository` → `ProductRepository`, `ProcessOrderUseCase` → `CreateProductUseCase`, `OrderController` → `ProductController`. Manual DI wiring via `ApplicationConfig` as per project convention.

## Architecture Decisions

### Decision: Keep `int` for price instead of `BigDecimal`

**Choice**: Retain `int price` in domain and entity
**Alternatives considered**: Migrate to `BigDecimal` per spec suggestion
**Rationale**: Entire codebase uses `int` for price (Product, ProductEntity, ProductRequest). Changing breaks Order flow. Future HU can migrate all at once.

### Decision: Static factory for backward-compatible Product construction

**Choice**: Add `Product.create(name, type, price, category)` factory + keep existing 3-arg constructor
**Alternatives considered**: Break existing constructor; builder pattern
**Rationale**: `Order` and `TaskDecomposer` construct `Product(name, type, price)` throughout. New factory method for catalog creation preserves all existing call sites. `reconstruct()` factory for hydrating from persistence (matches `Order.reconstruct()` pattern).

### Decision: New `ProductStatus` enum in domain model

**Choice**: Standalone enum, not embedded in `Product`
**Alternatives considered**: String status field; boolean `active` flag
**Rationale**: Follows `OrderStatus`/`TaskStatus` enum pattern already in domain. Enables future lifecycle transitions.

### Decision: `DuplicateProductException` as application-layer exception

**Choice**: Custom checked exception in `application/exepcions/`
**Alternatives considered**: Reuse `IllegalArgumentException`; domain exception
**Rationale**: Uniqueness is an application concern (repository check). Follows `DuplicateEmailException`/`DuplicateUsernameException` pattern. `GlobalExceptionHandler` maps it to 409 Conflict.

## Data Flow

```
POST /api/products
       │
       ▼
ProductController ──→ CreateProductPort.execute(command)
       │                        │
       │                        ▼
       │               CreateProductUseCase
       │                 ├─ validate fields
       │                 ├─ productRepo.existsByName(name)?
       │                 │    → YES: throw DuplicateProductException
       │                 ├─ Product.create(name, type, price, category)
       │                 └─ productRepo.save(product)
       │                        │
       ▼                        ▼
CreateProductResponse   ProductRepositoryAdapter
                         ├─ mapper.toEntity(product)
                         ├─ jpaRepo.save(entity)
                         └─ mapper.toDomain(saved)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/model/Product.java` | Modify | Add `id`, `status`, `category` fields; `create()` and `reconstruct()` factories; keep 3-arg constructor |
| `domain/model/ProductStatus.java` | Create | Enum: `ACTIVE`, `INACTIVE` |
| `application/ports/in/CreateProductPort.java` | Create | Input port: `Product execute(CreateProductCommand cmd)` |
| `application/ports/out/ProductRepository.java` | Create | Output port: `save(Product)`, `existsByName(String)` |
| `application/usecases/CreateProductUseCase.java` | Create | Implements `CreateProductPort`; validates, checks uniqueness, delegates to repo |
| `application/usecases/dto/CreateProductCommand.java` | Create | Record with `name`, `price`, `type`, `category` |
| `application/exepcions/DuplicateProductException.java` | Create | Extends `RuntimeException`; follows `DuplicateEmailException` pattern |
| `infrastructure/persistence/jpa/entities/ProductEntity.java` | Modify | Add `status`, `category` columns; `@Column(unique=true)` on `name` |
| `infrastructure/persistence/jpa/ProductJpaRepository.java` | Create | Extends `JpaRepository<ProductEntity, Long>`; `boolean existsByName(String)` |
| `infrastructure/persistence/adapters/ProductRepositoryAdapter.java` | Create | Implements `ProductRepository`; follows `OrderRepositoryAdapter` pattern |
| `infrastructure/persistence/mappers/ProductEntityMapper.java` | Modify | Add mappings for `id`, `status`, `category`; keep existing `toTaskProductEntity` |
| `infrastructure/rest/ProductController.java` | Create | `POST /api/products` → 201 + Location header |
| `infrastructure/rest/dto/CreateProductRequest.java` | Create | Record: `name`, `price`, `type`, `category` |
| `infrastructure/rest/dto/CreateProductResponse.java` | Create | Record: `id`, `name`, `price`, `type`, `status`, `category` |
| `infrastructure/rest/exception/GlobalExceptionHandler.java` | Modify | Add `DuplicateProductException` → 409 handler |
| `infrastructure/config/ApplicationConfig.java` | Modify | Wire `CreateProductUseCase`, `CreateProductPort` beans |

## Interfaces / Contracts

```java
// Input Port
public interface CreateProductPort {
    Product execute(CreateProductCommand command);
}

// Output Port
public interface ProductRepository {
    Product save(Product product);
    boolean existsByName(String name);
}

// Command DTO
public record CreateProductCommand(String name, int price, String type, String category) {}

// REST Response
public record CreateProductResponse(Long id, String name, int price, String type, String status, String category) {}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Domain Unit | `Product.create()` validation, `ProductStatus` defaults | JUnit `@Tag("unit")`, no mocks, `assertThrows` for invalid inputs |
| UseCase Unit | `CreateProductUseCase` happy path, duplicate detection, field validation | Mockito mocks for `ProductRepository`, `@Tag("unit")` |
| Mapper Unit | `ProductEntityMapper` new field mappings | Direct instantiation, assert field-by-field |
| Integration | Full POST /api/products flow: creation, duplicates, validation errors | `@SpringBootTest` + `@Tag("integration")` + H2, `TestRestTemplate` |

## Migration / Rollout

1. **Schema**: JPA auto-ddl handles `status` + `category` columns + unique constraint on `name` in H2/dev. Production: add Flyway migration `V2__add_product_catalog_columns.sql`
2. **Backward compat**: Existing `Product(name, type, price)` constructor unchanged — order processing unaffected
3. **Feature toggle**: `feature.product-catalog.enabled` property guards `ProductController` via `@ConditionalOnProperty`
4. **Rollback**: Remove migration; disable feature toggle. Zero impact on order flow.

## Open Questions

- [ ] Should product names be case-insensitive unique? (Spec says case-sensitive; verify with PO)
- [ ] Flyway vs JPA auto-ddl for schema changes in current environment?
