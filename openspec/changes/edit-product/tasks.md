# Tasks: HU-02 — Editar producto existente

## Phase 1: Foundation / Domain

- [x] 1.1 Modify `src/main/java/com/foodtech/kitchen/domain/model/Product.java` to add `description`, extend `create()` / `reconstruct()`, add `getDescription()`, and implement immutable `update(...)` validation.
- [x] 1.2 Modify `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/entities/ProductEntity.java` and `src/main/java/com/foodtech/kitchen/infrastructure/persistence/mappers/ProductEntityMapper.java` to persist/map `description`.
- [x] 1.3 Modify `src/main/java/com/foodtech/kitchen/application/ports/out/ProductRepository.java`, `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/ProductJpaRepository.java`, and `src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters/ProductRepositoryAdapter.java` to support `findByUuid(UUID)`.

## Phase 2: Backend Application / API

- [x] 2.1 Create `src/main/java/com/foodtech/kitchen/application/usecases/dto/UpdateProductCommand.java` and `src/main/java/com/foodtech/kitchen/application/ports/in/UpdateProductPort.java`.
- [x] 2.2 Create `src/main/java/com/foodtech/kitchen/application/exepcions/ProductNotFoundException.java` and `src/main/java/com/foodtech/kitchen/application/usecases/UpdateProductUseCase.java` to load, validate, update, and save products.
- [x] 2.3 Create `src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/UpdateProductRequest.java` and `src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/UpdateProductResponse.java`.
- [x] 2.4 Modify `src/main/java/com/foodtech/kitchen/infrastructure/rest/ProductController.java`, `src/main/java/com/foodtech/kitchen/infrastructure/rest/exception/GlobalExceptionHandler.java`, and `src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java` to expose `PUT /api/products/{uuid}`, return `200/400/404`, and wire the new use case.

## Phase 3: Frontend Product Editing

- [x] 3.1 Modify `../FoodTech-Front/src/models/Product.ts` and `../FoodTech-Front/src/services/productService.ts` to add `description`, update request/response contracts, and implement `updateProduct(id, request)`.
- [x] 3.2 Create `../FoodTech-Front/src/hooks/useUpdateProduct.ts` to manage submit state, success state, and CA5 error retention.
- [x] 3.3 Create `../FoodTech-Front/src/components/admin/EditProductForm.tsx` with preloaded fields, required-field validation, status selector, and retry-safe form state.
- [x] 3.4 Modify `../FoodTech-Front/src/views/AdminView.tsx` to list catalog products, open the edit form/modal, refresh data after success, and keep inactive products visible only in admin.
- [x] 3.5 Modify waiter-side product selection flow in `../FoodTech-Front/src/views/WaiterView.tsx` and related hooks/services to exclude products with `status = INACTIVE` from new orders.

## Phase 4: Testing / Verification

- [x] 4.1 Add/extend `src/test/java/com/foodtech/kitchen/domain/model/ProductTest.java` for `description` support and `update(...)` validation (blank name, null type/status, invalid price, null description).
- [x] 4.2 Create `src/test/java/com/foodtech/kitchen/application/usecases/UpdateProductUseCaseTest.java` for success, not-found, and inactive status scenarios.
- [x] 4.3 Create/extend backend integration tests for `PUT /api/products/{uuid}` covering `200 OK`, `400 Bad Request`, and `404 Not Found`.
- [x] 4.4 Create frontend tests for `productService.updateProduct`, `useUpdateProduct`, and `EditProductForm` covering CA1–CA5 including retained form state on server failure.
- [x] 4.5 Run backend `./gradlew test` and frontend `npm test -- --run`, then fix regressions until both suites pass.
