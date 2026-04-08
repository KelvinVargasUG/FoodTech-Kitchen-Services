# Design: HU-02 — Editar producto existente

## Technical Approach

Add a `description` field to the domain `Product` and introduce an `update()` method that returns a new immutable instance. Expose `PUT /api/products/{uuid}` through `UpdateProductUseCase` → `ProductRepository.findByUuid()`. Follow existing patterns: manual DI in `ApplicationConfig`, port/adapter hexagonal structure, and `exepcions` package naming.

## Architecture Decisions

| Decision | Choice | Alternative | Rationale |
|----------|--------|-------------|-----------|
| HTTP verb | `PUT` (full replacement) | `PATCH` (partial) | Simpler: all fields always sent; matches frontend single-form UX |
| Domain update | Immutable `Product.update(...)` returning new instance | Mutable setters | Preserves existing immutability pattern in `Product.java` |
| Description field | `String description` nullable, MAY be null/blank | Required field | Spec CA3 explicitly excludes description from required validation |
| Lookup key | UUID (path variable) | Long id | UUID is the public-facing identifier; Long id is internal JPA |
| Not-found exception | New `ProductNotFoundException` in `exepcions/` | Reuse `OrderNotFoundException` | Distinct error message per domain; follows existing `OrderNotFoundException` pattern |

## Data Flow

```
PUT /api/products/{uuid}
         │
    ProductController.updateProduct()
         │
    UpdateProductCommand (name, description, type, category, price, status)
         │
    UpdateProductUseCase.execute(uuid, command)
         ├── productRepository.findByUuid(uuid)  → Product or throw ProductNotFoundException
         ├── product.update(name, desc, type, category, price, status) → new Product
         └── productRepository.save(updatedProduct) → Product
         │
    200 OK + UpdateProductResponse
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/model/Product.java` | Modify | Add `description` field, `getDescription()`, `update(...)` method, expand `create()`/`reconstruct()` signatures |
| `infrastructure/persistence/jpa/entities/ProductEntity.java` | Modify | Add `description` column |
| `infrastructure/persistence/mappers/ProductEntityMapper.java` | Modify | Map `description` in `toProductEntity()` and `toDomain()` |
| `application/ports/in/UpdateProductPort.java` | Create | Interface: `Product execute(UUID id, UpdateProductCommand cmd)` |
| `application/usecases/dto/UpdateProductCommand.java` | Create | Record/class: name, description, type, category, price, status |
| `application/usecases/UpdateProductUseCase.java` | Create | Implements `UpdateProductPort`, calls `findByUuid` → `product.update()` → `save()` |
| `application/exepcions/ProductNotFoundException.java` | Create | `"Product not found with id: " + uuid` |
| `application/ports/out/ProductRepository.java` | Modify | Add `Optional<Product> findByUuid(UUID id)` |
| `infrastructure/persistence/jpa/ProductJpaRepository.java` | Modify | Add `Optional<ProductEntity> findByUuid(String uuid)` |
| `infrastructure/persistence/adapters/ProductRepositoryAdapter.java` | Modify | Implement `findByUuid()` |
| `infrastructure/rest/ProductController.java` | Modify | Add `@PutMapping("/{id}")` method, inject `UpdateProductPort` |
| `infrastructure/rest/dto/UpdateProductRequest.java` | Create | name, description(optional), type, category, price, status |
| `infrastructure/rest/dto/UpdateProductResponse.java` | Create | id(UUID), name, description, type, category, price, status |
| `infrastructure/rest/exception/GlobalExceptionHandler.java` | Modify | Handle `ProductNotFoundException` → 404 |
| `infrastructure/config/ApplicationConfig.java` | Modify | Wire `UpdateProductPort` bean |

## Interfaces / Contracts

```java
// Port in
public interface UpdateProductPort {
    Product execute(UUID id, UpdateProductCommand command);
}

// Port out (addition to existing)
Optional<Product> findByUuid(UUID id);

// Domain method
public Product update(String name, String description, ProductType type, String category, int price, ProductStatus status)
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | `Product.update()` validation (blank name, null type, price≤0, null status, null description ok) | `ProductTest.java` |
| Unit | `UpdateProductUseCase` happy/not-found paths | Mock `ProductRepository`, verify `findByUuid` + `save` |
| Integration | `PUT /api/products/{uuid}` → 200, 400, 404 | `@SpringBootTest` with H2, `TestRestTemplate` |

## Migration / Rollout

No migration required — `ddl-auto: create` handles schema. The `description` column is nullable, so existing rows get `NULL` automatically.
