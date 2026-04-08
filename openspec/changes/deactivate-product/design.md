# Design: HU-03: Desactivar producto del catálogo (Borrado Lógico)

## Technical Approach

Add domain methods and use cases for toggling product status (`ACTIVE`/`INACTIVE`). Expose two endpoints (`PATCH /api/products/{uuid}/deactivate` and `PATCH /api/products/{uuid}/activate`). Extend `ProductRepository` port with `findByUuid` and `findAllByStatus`. Frontend adds toggle buttons in Admin view and filters by active status in Waiter view. Historical order integrity is already guaranteed by the `order_products` snapshot table.

## Architecture Decisions

| Decision | Choice | Alternatives | Rationale |
|----------|--------|-------------|-----------|
| Endpoint style | Two explicit PATCH endpoints (`/activate`, `/deactivate`) | Single PATCH with status body | Explicit endpoints are simpler, self-documenting, and prevent invalid status transitions |
| Domain method | `Product.deactivate()` / `Product.activate()` returning new Product | Setter on status field | Immutable domain model — follows existing `Product.create()` / `Product.reconstruct()` pattern |
| Query filtering | Backend filters by status in repository query | Frontend filters client-side | Reduces payload, prevents information leakage, aligns with spec |

## Data Flow

```
Admin UI ──PATCH──→ ProductController ──→ ChangeProductStatusUseCase
                                              │
                                     ProductRepository.findByUuid()
                                              │
                                     Product.deactivate() / .activate()
                                              │
                                     ProductRepository.save()
                                              │
                                     ←── 200 OK + updated Product
```

```
Waiter UI ──GET──→ ProductController ──→ GetActiveProductsUseCase
                                              │
                                     ProductRepository.findAllByStatus(ACTIVE)
                                              │
                                     ←── 200 OK + List<Product>
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/model/Product.java` | Modify | Add `deactivate()` and `activate()` methods returning new Product |
| `application/ports/in/ChangeProductStatusPort.java` | Create | Input port for status toggle |
| `application/ports/in/GetActiveProductsPort.java` | Create | Input port for listing active products |
| `application/usecases/ChangeProductStatusUseCase.java` | Create | Implements status change logic |
| `application/usecases/GetActiveProductsUseCase.java` | Create | Returns only active products |
| `application/ports/out/ProductRepository.java` | Modify | Add `findByUuid(UUID)`, `findAllByStatus(ProductStatus)` |
| `infrastructure/persistence/jpa/ProductJpaRepository.java` | Modify | Add `findByUuid(String)`, `findAllByStatus(ProductStatus)` |
| `infrastructure/persistence/adapters/ProductRepositoryAdapter.java` | Modify | Implement new repository methods |
| `infrastructure/rest/ProductController.java` | Modify | Add PATCH and GET endpoints |
| `infrastructure/rest/dto/ProductResponse.java` | Create | Reusable response DTO for list/single product |

## Interfaces / Contracts

```java
// Product domain methods (immutable — return new instance)
public Product deactivate() { ... }
public Product activate() { ... }

// Port — change status
public interface ChangeProductStatusPort {
    Product deactivate(UUID productId);
    Product activate(UUID productId);
}

// Port — list active
public interface GetActiveProductsPort {
    List<Product> execute();
}

// Repository additions
Optional<Product> findByUuid(UUID uuid);
List<Product> findAllByStatus(ProductStatus status);
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (Domain) | `Product.deactivate()` / `activate()` state transitions, validation | JUnit — mock-free |
| Unit (UseCase) | `ChangeProductStatusUseCase` with mocked repository | JUnit + Mockito |
| Unit (UseCase) | `GetActiveProductsUseCase` returns only ACTIVE | JUnit + Mockito |
| Integration | Full HTTP flow: PATCH deactivate → GET active list excludes it | SpringBootTest + MockMvc |
| Integration | Orders in progress unaffected by deactivation | SpringBootTest + full context |

## Migration / Rollout

No migration required. The `ProductStatus` enum and `status` column already exist in the schema. No new columns or tables needed.

## Open Questions

- (None — all blockers resolved during analysis)