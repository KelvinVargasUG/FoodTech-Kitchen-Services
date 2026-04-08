# Tasks: HU-03: Desactivar producto del catálogo (Borrado Lógico)

## Phase 1: Backend Foundation

- [ ] 1.1 Modify `src/main/java/com/foodtech/kitchen/domain/model/Product.java` to add immutable `deactivate()` and `activate()` methods that preserve `id`, `name`, `type`, `category` and `price`.
- [ ] 1.2 Create `src/main/java/com/foodtech/kitchen/application/ports/in/ChangeProductStatusPort.java` with `deactivate(UUID)` and `activate(UUID)` contracts.
- [ ] 1.3 Create `src/main/java/com/foodtech/kitchen/application/ports/in/GetActiveProductsPort.java` with a list operation for active catalog products.
- [ ] 1.4 Extend `src/main/java/com/foodtech/kitchen/application/ports/out/ProductRepository.java` with `findByUuid(UUID)` and `findAllByStatus(ProductStatus)`.
- [ ] 1.5 Update `src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/ProductJpaRepository.java` to query by `uuid` and `status`.
- [ ] 1.6 Update `src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters/ProductRepositoryAdapter.java` and `src/main/java/com/foodtech/kitchen/infrastructure/persistence/mappers/ProductEntityMapper.java` to support the new repository reads.

## Phase 2: Backend Use Cases and API

- [ ] 2.1 Create `src/main/java/com/foodtech/kitchen/application/usecases/ChangeProductStatusUseCase.java` to load a product, toggle status and save it.
- [ ] 2.2 Create `src/main/java/com/foodtech/kitchen/application/usecases/GetActiveProductsUseCase.java` to return only `ACTIVE` products.
- [ ] 2.3 Create `src/main/java/com/foodtech/kitchen/application/exepcions/ProductNotFoundException.java` for missing UUID lookups.
- [ ] 2.4 Create `src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/ProductResponse.java` as the reusable REST payload for list and single-product responses.
- [ ] 2.5 Modify `src/main/java/com/foodtech/kitchen/infrastructure/rest/ProductController.java` to add `PATCH /api/products/{uuid}/deactivate`, `PATCH /api/products/{uuid}/activate` and `GET /api/products?status=ACTIVE`.
- [ ] 2.6 Modify `src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java` to wire the new ports and use cases.
- [ ] 2.7 Update the global REST error handler used by product endpoints to map `ProductNotFoundException` to `404`.

## Phase 3: Frontend Integration

- [ ] 3.1 Extend `../FoodTech-Front/src/models/Product.ts` with response/request types needed for catalog listing and status toggling.
- [ ] 3.2 Modify `../FoodTech-Front/src/services/productService.ts` to add `getProducts(status?)`, `deactivateProduct(id)` and `activateProduct(id)`.
- [ ] 3.3 Extend `../FoodTech-Front/src/services/productService.test.ts` with RED-first tests for the new service methods and backend path contracts.
- [ ] 3.4 Modify `../FoodTech-Front/src/views/AdminView.tsx` and `../FoodTech-Front/src/components/admin/CreateProductForm.tsx` to show the catalog list plus `Activar/Desactivar` actions and success feedback.
- [ ] 3.5 Modify `../FoodTech-Front/src/views/WaiterView.tsx` to load products from the API instead of `src/helpers/menuData.ts`, ensuring only active products reach `ProductGrid`.

## Phase 4: Testing and Verification

- [ ] 4.1 Extend `src/test/java/com/foodtech/kitchen/domain/model/ProductTest.java` with RED/GREEN tests for `deactivate()` and `activate()` transitions.
- [ ] 4.2 Create `src/test/java/com/foodtech/kitchen/application/usecases/ChangeProductStatusUseCaseTest.java` for deactivate, activate and not-found scenarios.
- [ ] 4.3 Create `src/test/java/com/foodtech/kitchen/application/usecases/GetActiveProductsUseCaseTest.java` to verify only `ACTIVE` products are returned.
- [ ] 4.4 Extend `src/test/java/com/foodtech/kitchen/infrastructure/rest/ProductControllerIntegrationTest.java` for CA1, CA2 and CA5: deactivate/activate flow plus active-product filtering.
- [ ] 4.5 Add an integration test covering CA3/CA4 to verify orders already created keep product snapshots after the catalog product becomes `INACTIVE`.
- [ ] 4.6 Run backend and frontend test suites for product flows, then verify the admin toggle and waiter catalog behavior manually.