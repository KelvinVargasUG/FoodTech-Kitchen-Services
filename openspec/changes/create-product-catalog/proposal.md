# Proposal: Product Catalog Management

## Intent

Enable administrators to dynamically create and manage products in the catalog, eliminating hardcoded product limitations. Each product must be assigned to a specific kitchen station (BAR, HOT_KITCHEN, COLD_KITCHEN) to ensure automatic task routing when orders are processed.

## Scope

### In Scope
- REST API endpoint to create products with validation
- Domain entity enhancement: add ID, category, status (Active/Inactive)
- Product repository port and JPA adapter
- Duplicate name validation
- Mandatory field validation (name, price, category, station)
- Station assignment via ProductType mapping

### Out of Scope
- Product updates, deletion, or listing endpoints (future HUs)
- Inventory or stock management
- Dynamic pricing rules or promotions
- Product images or descriptions

## Approach

Extend existing hexagonal architecture following established patterns:

1. **Domain Layer**: Enhance Product entity as aggregate root with ID, status, category
2. **Application Layer**: Create `CreateProductUseCase`, `ProductRepository` port
3. **Infrastructure Layer**: Implement `ProductRepositoryAdapter`, `ProductJpaRepository`, `ProductController`
4. **Validation**: Domain-level validation for mandatory fields; repository-level uniqueness check

Follow existing patterns: OrderRepository → ProductRepository, ProcessOrderUseCase → CreateProductUseCase

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/model/Product.java` | Modified | Add ID, status, category fields; make mutable aggregate root |
| `application/ports/out/ProductRepository.java` | New | Repository port with save() and existsByName() |
| `application/usecases/CreateProductUseCase.java` | New | Use case implementing business logic |
| `infrastructure/persistence/adapters/ProductRepositoryAdapter.java` | New | JPA adapter |
| `infrastructure/rest/ProductController.java` | New | REST endpoint POST /api/products |
| `infrastructure/persistence/jpa/entities/ProductEntity.java` | Modified | Add status, category fields; unique constraint on name |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| ProductType-Station mapping breaks | Low | ProductType enum already has getStation(); maintain this mapping |
| Concurrent duplicate creation | Medium | Database unique constraint on name; optimistic locking |
| Existing order processing depends on immutable Product | Low | Order processing uses ProductType, not Product entity directly |

## Rollback Plan

1. Database: Revert migration adding status/category columns to product table
2. Code: Feature toggle `feature.product-creation.enabled=false` disables endpoint
3. Zero impact on existing order processing (uses separate ProductType enum)

## Dependencies

- Database migration tool (Flyway or Liquibase assumed present)
- Spring Boot 3.2.1, Spring Data JPA

## Success Criteria

- [ ] CA1: Product created with valid data; status defaults to "Active"; appears in catalog
- [ ] CA2: Validation errors shown for missing mandatory fields
- [ ] CA3: Duplicate name prevented with descriptive error message
- [ ] Integration test covers all acceptance criteria
- [ ] Code coverage ≥ 80% per DevOps guidelines
