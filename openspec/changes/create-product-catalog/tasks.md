# Tasks: Product Catalog Management

## Phase 1: Domain Foundation

- [x] 1.1 RED: Add `src/test/java/com/foodtech/kitchen/domain/model/ProductTest.java` for `Product.create()` defaults, mandatory-field failures, and `ProductType.getStation()` behavior.
- [x] 1.2 GREEN: Update `src/main/java/com/foodtech/kitchen/domain/model/Product.java` and create `src/main/java/com/foodtech/kitchen/domain/model/ProductStatus.java` with `id`, `status`, `category`, `create()`, `reconstruct()`, and the preserved 3-arg constructor.
- [x] 1.3 REFACTOR: Update `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/entities/ProductEntity.java` and `src/main/java/com/foodtech/kitchen/infrastructure/persistence/mappers/ProductEntityMapper.java` to align `id/status/category` without breaking `toTaskProductEntity()`.

## Phase 2: Application Flow

- [x] 2.1 RED: Add `src/test/java/com/foodtech/kitchen/application/usecases/CreateProductUseCaseTest.java` for happy path, duplicate names, zero price, and missing category.
- [x] 2.2 GREEN: Create `src/main/java/com/foodtech/kitchen/application/ports/in/CreateProductPort.java`, `src/main/java/com/foodtech/kitchen/application/ports/out/ProductRepository.java`, `src/main/java/com/foodtech/kitchen/application/usecases/dto/CreateProductCommand.java`, `src/main/java/com/foodtech/kitchen/application/exepcions/DuplicateProductException.java`, and `src/main/java/com/foodtech/kitchen/application/usecases/CreateProductUseCase.java`.
- [x] 2.3 REFACTOR: Wire `CreateProductUseCase` through `src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java` so the port follows the project’s manual `@Bean` convention.

## Phase 3: Persistence and API Wiring

- [x] 3.1 RED: Add `src/test/java/com/foodtech/kitchen/infrastructure/persistence/mappers/ProductEntityMapperTest.java` and `src/test/java/com/foodtech/kitchen/infrastructure/persistence/adapters/ProductRepositoryAdapterTest.java` for field mapping, generated IDs, and `existsByName()`.
- [x] 3.2 GREEN: Create `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/ProductJpaRepository.java` and `src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters/ProductRepositoryAdapter.java`; finish `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/entities/ProductEntity.java` with unique `name`, `status`, and `category`.
- [x] 3.3 RED: Add `src/test/java/com/foodtech/kitchen/infrastructure/rest/ProductControllerIntegrationTest.java` for `POST /api/products` success (`201` + `Location`), validation `400`, and duplicate `409`.
- [x] 3.4 GREEN: Create `src/main/java/com/foodtech/kitchen/infrastructure/rest/ProductController.java`, `src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/CreateProductRequest.java`, and `src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/CreateProductResponse.java`; update `src/main/java/com/foodtech/kitchen/infrastructure/rest/exception/GlobalExceptionHandler.java` for `DuplicateProductException`.
- [x] 3.5 REFACTOR: Guard `src/main/java/com/foodtech/kitchen/infrastructure/rest/ProductController.java` with `@ConditionalOnProperty` and add `feature.product-catalog.enabled` in `src/main/resources/application.yaml`.

## Phase 4: Verification and Rollout

- [x] 4.1 RED/GREEN: Extend `src/test/java/com/foodtech/kitchen/infrastructure/rest/exception/GlobalExceptionHandlerTest.java` for duplicate-product `409` and malformed product payload `400` responses.
- [x] 4.2 GREEN: Add `src/main/resources/db/migration/V2__add_product_catalog_columns.sql` if Flyway is active; otherwise document JPA `ddl-auto` reliance in `openspec/changes/create-product-catalog/design.md` during implementation.
- [x] 4.3 Verify `./gradlew test` against the new tests and update `openspec/changes/create-product-catalog/tasks.md` checkboxes during `sdd-apply`.
