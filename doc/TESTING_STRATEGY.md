# Testing Strategy - FoodTech Kitchen Services (Backend)

---

# PARTE 1: TEORÍA

## 1.1 TDD (Red - Green - Refactor)

**¿Qué es TDD?**
Desarrollo dirigido por pruebas: escribimos el test primero (RED), luego el código mínimo para pasar (GREEN), luego refactorizamos.

| Fase | Qué hacemos | Resultado |
|------|-------------|-----------|
| **RED** | Escribimos el test antes del código | Test falla |
| **GREEN** | Escribimos código mínimo para pasar | Test pasa |
| **REFACTOR** | Mejoramos código sin romper tests | Tests siguen pasando |

**Ejemplo Backend:**
1. RED: Escribo test "createProduct debe lanzar DuplicateProductException si ya existe" → falla
2. GREEN: Implemento la validación en `CreateProductUseCase` → test pasa
3. REFACTOR: Extraigo lógica a método privado → tests siguen pasando

---

## 1.2 ¿Qué es Verificar?

- **Fase TDD:** GREEN
- **Pregunta:** ¿El código funciona correctamente?
- **Ejemplos:** ¿El UseCase persiste el producto? ¿El endpoint retorna 201?

---

## 1.3 ¿Qué es Validar?

- **Fase RED → GREEN:** Protegemos el negocio
- **Pregunta:** ¿El negocio está protegido?
- **Ejemplos:** ¿Producto duplicado = excepción? ¿Sin JWT = 401? ¿Estación inválida = error de validación?

---

## 1.4 QA vs Testing

| Concepto | Descripción |
|----------|-------------|
| **Testing** | Ejecutar suites JUnit para verificar que el código funciona |
| **QA (Quality Assurance)** | Estrategia multilevel: dominio + aplicación + infraestructura + integración |

---

## 1.5 Arquitectura Hexagonal y Testing

Este proyecto sigue **Clean Architecture / Hexagonal**. Los tests se organizan según cada capa:

```
src/test/java/
 └── com.foodtech.kitchen/
      ├── domain/          ← Caja blanca pura (sin mocks de infra)
      │    ├── model/      ← Entidades y reglas de dominio
      │    ├── commands/   ← Comandos de preparación (Reactor)
      │    └── services/   ← TaskDecomposer, CommandFactory
      ├── application/     ← Use cases con mocks de puertos
      │    └── usecases/
      ├── infrastructure/  ← Adaptadores, REST, seguridad
      │    ├── persistence/
      │    ├── rest/        ← Tests de integración con MockMvc
      │    └── security/
      └── integration/     ← Flujos end-to-end (H2 + Spring context)
```

---

## 1.6 Workflow TDD Backend

| Paso | Fase | Acción | Herramienta | Resultado |
|------|------|--------|-------------|-----------|
| 1 | - | Leer HU / spec | docs/ | Entender dominio |
| 2 | **RED** | Escribir test de dominio | JUnit 5 | Test falla ✗ |
| 3 | **RED** | Verificar falla | `./gradlew test` | Confirmar ✗ |
| 4 | **GREEN** | Implementar mínimo | Java | Test pasa ✓ |
| 5 | **GREEN** | Añadir test de use case | Mockito | Test pasa ✓ |
| 6 | **REFACTOR** | Mejorar sin romper | - | Tests pasan ✓ |
| 7 | - | Test de integración REST | MockMvc + H2 | 201/200/400 ✓ |

---

# PARTE 2: DOMAIN MODEL TESTS

## 2.1 ProductTest (32 tests)

**Archivo:** `src/test/java/.../domain/model/ProductTest.java`
**Tag:** `@Tag("unit")`

**Qué cubre:** Todas las reglas de dominio del modelo `Product`. Sin mocks, sin Spring.

| Grupo | Tests | Qué valida |
|-------|-------|-----------|
| `create()` defaults | 6 | Status ACTIVE, UUID no-null, nombre/tipo/categoría/precio correctos |
| `create()` validaciones | 8 | Nombre vacío → excepción, precio ≤ 0 → excepción, tipo null → excepción |
| `update()` | 7 | Actualiza campos, respeta nulls, mantiene ID original |
| `activate()` / `deactivate()` | 5 | Cambia status, producto ya activo no falla, inmutabilidad |
| `withStatus()` | 6 | Transiciones de estado válidas e inválidas |

**Ejemplo (Verificar):**
```java
@Test
@DisplayName("create() should set status ACTIVE by default")
void create_setsStatusActive() {
    Product product = Product.create("Pizza", ProductType.HOT_DISH, "Italian", 10);
    assertEquals(ProductStatus.ACTIVE, product.getStatus());
}
```

**Ejemplo (Validar):**
```java
@Test
@DisplayName("create() should throw when name is blank")
void create_blankName_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> Product.create("", ProductType.HOT_DISH, "Italian", 10));
}
```

---

## 2.2 OrderTest (13 tests)

**Archivo:** `src/test/java/.../domain/model/OrderTest.java`
**Tag:** `@Tag("unit")`

**Qué cubre:** Construcción y reglas del modelo `Order`.

| Test | Tipo | Qué valida |
|------|------|-----------|
| tableNumber null → IllegalArgument | Validar | Protege integridad del pedido |
| tableNumber blank → IllegalArgument | Validar | Protege integridad del pedido |
| productos null → IllegalArgument | Validar | Pedido sin productos no válido |
| order creado con items correctos | Verificar | Construcción exitosa |
| estado inicial PENDING | Verificar | FSM del pedido |

---

## 2.3 TaskTest (3 tests)

**Archivo:** `src/test/java/.../domain/model/TaskTest.java`  
**Tag:** `@Tag("unit")`

Verifica la creación de `Task` con estación, estado inicial y relación con la orden.

---

## 2.4 Upload Domain Models (26 tests)

| Clase de test | Tests | Qué cubre |
|---------------|-------|-----------|
| `UploadSessionTest` | 9 | `initiate()`, `withUploadStatus()`, `withProcessingResult()`, validaciones |
| `ProductStagingTest` | 6 | `fromRawRow()`, validación de campos, mapeado correcto |
| `UploadChunkTest` | 4 | `receive()`, checksum, filePath, índice |
| `UploadedFileTest` | 3 | `assemble()`, tamaño, path |
| `ErrorRecordTest` | 4 | `of()`, rowNumber, errorCode, rawData |

---

# PARTE 3: DOMAIN SERVICES TESTS

## 3.1 TaskDecomposerTest (10 tests)

**Archivo:** `src/test/java/.../domain/services/TaskDecomposerTest.java`  
**Tag:** `@Tag("unit")`

**Qué cubre:** Lógica de descomposición de un pedido en tareas por estación.

| Test | Tipo | Qué valida |
|------|------|-----------|
| Pedido con DRINK → tarea BAR | Verificar | Mapeo estación correcto |
| Pedido con HOT_DISH → tarea HOT_KITCHEN | Verificar | Mapeo estación correcto |
| Pedido con COLD_DISH → tarea COLD_KITCHEN | Verificar | Mapeo estación correcto |
| Pedido mixto → múltiples tareas | Verificar | Una tarea por estación |
| Pedido vacío → lista vacía | Validar | Edge case sin productos |

---

## 3.2 CommandFactoryTest (4 tests)

**Archivo:** `src/test/java/.../domain/services/CommandFactoryTest.java`

Verifica que la fábrica retorna el comando correcto (`PrepareHotDishCommand`, `PrepareDrinkCommand`, `PrepareColdDishCommand`) según el `ProductType`.

---

## 3.3 PrepareCommand Tests (7 tests)

| Clase | Tests | Qué cubre |
|-------|-------|-----------|
| `PrepareHotDishCommandTest` | 2 | Ejecución reactiva, delay simulado |
| `PrepareDrinkCommandTest` | 3 | Ejecución reactiva, delay simulado |
| `PrepareColdDishCommandTest` | 2 | Ejecución reactiva, delay simulado |

**Patrón:** Usan `StepVerifier` de Project Reactor para verificar emisión correcta del `Mono<Void>`.

```java
@Test
void execute_shouldCompleteSuccessfully() {
    PrepareHotDishCommand cmd = new PrepareHotDishCommand(task);
    StepVerifier.create(cmd.execute())
        .verifyComplete();
}
```

---

# PARTE 4: APPLICATION USE CASES TESTS

## 4.1 Patrón: Mockito + @ExtendWith(MockitoExtension.class)

Todos los use case tests usan Mockito para mockear los puertos de salida (`ProductRepository`, `OrderRepository`, etc.), sin levantar Spring.

```java
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {
    @Mock  ProductRepository productRepository;
    @InjectMocks  CreateProductUseCase useCase;
}
```

## 4.2 Tabla de Use Cases

| Clase de Test | Tests | Use Case | Qué Valida |
|---------------|-------|----------|------------|
| `CreateProductUseCaseTest` | 4 | Crear producto | Creación exitosa, duplicado → excepción, repo.save llamado |
| `UpdateProductUseCaseTest` | 5 | Actualizar producto | Actualización, not found → excepción, campos opcionales |
| `ChangeProductStatusUseCaseTest` | 4 | Activar/desactivar | Transición válida, not found → excepción |
| `GetActiveProductsUseCaseTest` | 2 | Listar activos | Retorna lista, delegación al repo |
| `ProcessOrderUseCaseTest` | 3 | Procesar pedido | Creación de tareas, persistencia, decomposer llamado |
| `DeleteOrderUseCaseTest` | 2 | Eliminar pedido | Eliminación exitosa, not found → excepción |
| `GetOrderStatusUseCaseTest` | 5 | Estado del pedido | Retorna status, diferentes estados, not found |
| `GetTasksByStationUseCaseTest` | 2 | Tareas por estación | Filtrado correcto, lista vacía |
| `GetCompletedOrdersUseCaseTest` | 2 | Pedidos completados | Retorna lista, delegación |
| `StartTaskPreparationUseCaseTest` | 2 | Iniciar preparación | Cambio de estado, tarea not found |
| `OrderCompletionServiceTest` | 5 | Completar orden | Todas las tareas completas = orden completa, parcial no completa |
| `RequestOrderInvoiceUseCaseTest` | 4 | Solicitar factura | Payload correcto, orden not found, outbox creado |
| `AuthenticateUserUseCaseTest` | 4 | Autenticar usuario | Token generado, credenciales inválidas, user not found |
| `RegisterUserUseCaseTest` | 11 | Registrar usuario | Registro exitoso, duplicado → excepción, validaciones |
| `BulkUploadProductsUseCaseTest` | 16 | Carga masiva CSV | initSession, receiveChunk, completeAndProcess, errores |
| `InvoicePayloadBuilderTest` | 1 | Construcción payload | JSON correcto |

**Ejemplo (Validar - Regla de negocio):**
```java
@Test
@DisplayName("createProduct should throw DuplicateProductException when name exists")
void createProduct_duplicateName_throwsException() {
    when(productRepository.findByName("Pizza")).thenReturn(Optional.of(existing));
    assertThrows(DuplicateProductException.class,
        () -> useCase.createProduct(command));
    verify(productRepository, never()).save(any());
}
```

**Ejemplo (Verificar):**
```java
@Test
@DisplayName("createProduct should save and return the created product")
void createProduct_validCommand_savesAndReturns() {
    when(productRepository.findByName(any())).thenReturn(Optional.empty());
    when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    Product result = useCase.createProduct(command);
    assertNotNull(result.getId());
    verify(productRepository).save(any(Product.class));
}
```

---

# PARTE 5: INFRASTRUCTURE TESTS

## 5.1 Persistence Adapters (53 tests)

**Patrón:** Tests unitarios que mockean los repositorios JPA para probar el mapeo dominio ↔ entidad JPA.

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `ProductRepositoryAdapterTest` | 13 | findByName, save, findAll, findById, findByStatus |
| `OrderRepositoryAdapterTest` | 3 | save, findById, findByStatus |
| `TaskRepositoryAdapterTest` | 4 | save, findByOrderId, findByStation, updateStatus |
| `UploadRepositoryAdapterTest` | 19 | saveSession, findById, saveChunk, saveFile, saveStaging, saveError, findErrors |
| `UserRepositoryAdapterTest` | 13 | findByUsername, save, existsByUsername, findByEmail |
| `OutboxEventRepositoryAdapterTest` | 1 | save outbox event |

## 5.2 JPA y Mappers (7 tests)

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `TaskEntityTest` | 2 | Mapeo entidad JPA, campos |
| `TaskJpaRepositoryTest` | 2 | Queries JPQL custom |
| `OrderEntityMapperTest` | 2 | Mapeo dominio ↔ JPA |
| `OutboxEventEntityMapperTest` | 1 | Mapeo outbox event |

## 5.3 Execution (5 tests)

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `SyncCommandExecutorTest` | 3 | Ejecución síncrona de comandos, error handling |
| `ReactorAsyncCommandDispatcherTest` | 2 | Dispatch reactivo, paralelismo con Reactor |

## 5.4 Security y Serialización (5 tests)

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `JwtTokenGeneratorTest` | 3 | Generación de JWT, claims, expiración |
| `JacksonPayloadSerializerTest` | 2 | Serialización/deserialización JSON de payloads |

## 5.5 REST Mappers (5 tests)

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `OrderMapperTest` | 3 | Mapeo request → dominio, respuesta → DTO |
| `CompletedOrderMapperTest` | 2 | Mapeo pedidos completados |

---

# PARTE 6: INTEGRATION TESTS (REST + H2)

## 6.1 Patrón: @SpringBootTest + MockMvc + H2

Los tests de integración levantan el contexto Spring completo con base de datos H2 en memoria.

```java
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired TokenGenerator tokenGenerator;
    // Usa JWT real + H2 real
}
```

## 6.2 Tabla de Integration Tests

| Clase de Test | Tests | Endpoints | Qué Valida |
|---------------|-------|-----------|------------|
| `AuthControllerIntegrationTest` | 11 | `POST /auth/register`, `POST /auth/login` | Registro exitoso 201, login con JWT 200, usuario duplicado 409, credenciales inválidas 401 |
| `ProductControllerIntegrationTest` | 8 | `GET /api/products`, `POST /api/products`, `PATCH /api/products/{id}/status` | Lista, creación 201, duplicado 409, toggle status, sin auth 401 |
| `OrderControllerIntegrationTest` | 6 | `POST /api/orders`, `GET /api/orders/{id}/status`, `DELETE /api/orders/{id}` | Crear pedido 201, estado interno, eliminar, pedido inexistente 404 |
| `TaskControllerIntegrationTest` | 7 | `GET /api/tasks/station/{s}`, `PATCH /api/tasks/{id}/start` | Tareas por estación, iniciar preparación, sin auth 401 |
| `BulkUploadControllerTest` | 10 | `POST /api/upload/init`, `POST /{id}/chunk`, `POST /{id}/complete`, `GET /{id}/status`, `GET /{id}/errors`, `GET /template` | Flujo completo de carga, descarga de errores, plantilla |
| `SecurityIntegrationTest` | 5 | Todas | Sin token = 401, token inválido = 401, token expirado = 401, ruta pública = 200 |
| `GlobalExceptionHandlerTest` | 10 | Varias | ProductNotFound 404, DuplicateProduct 409, Unauthorized 401, ValidationError 400 |

---

## 6.3 Ejemplo: AuthControllerIntegrationTest

**Verificar (GREEN):**
```java
@Test
@DisplayName("POST /auth/register — 201 con token en respuesta")
void register_validRequest_returns201WithToken() throws Exception {
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"username":"nuevo","email":"n@test.com","password":"pass123"}"""))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").isNotEmpty());
}
```

**Validar (RED → GREEN):**
```java
@Test
@DisplayName("POST /auth/login — 401 cuando credenciales son inválidas")
void login_wrongPassword_returns401() throws Exception {
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"username":"admin","password":"wrong"}"""))
        .andExpect(status().isUnauthorized());
}
```

---

## 6.4 SecurityIntegrationTest

Tests que validan el sistema de seguridad JWT:

| Test | Tipo | Valida |
|------|------|--------|
| Request sin token → 401 | Validar | Rutas protegidas bloqueadas |
| Request token inválido → 401 | Validar | JWT corrupto rechazado |
| Request token expirado → 401 | Validar | JWT expirado rechazado |
| Ruta pública sin token → 200 | Verificar | `/auth/login` es accesible |
| Request con token válido → 200 | Verificar | JWT correcto permite acceso |

---

# PARTE 7: FULL INTEGRATION TEST

## 7.1 RegisterUserIntegrationTest (1 test)

**Archivo:** `src/test/java/.../integration/RegisterUserIntegrationTest.java`

Flujo completo end-to-end: registro de usuario → login → acceso a recurso protegido, usando Spring context completo y H2.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUserIntegrationTest {
    // Flujo: register → login → accceder a /api/products con JWT recibido
}
```

---

# PARTE 8: OUTBOX Y TRANSACCIONALES

| Clase de Test | Tests | Qué Cubre |
|---------------|-------|-----------|
| `OutboxEventTest` | 1 | Creación del evento de outbox |
| `TransactionalOrderCompletionServiceTest` | 1 | Transacción atómica en completion |
| `TransactionalRequestOrderInvoicePortTest` | 1 | Transacción atómica en facturación |

---

# PARTE 9: RESUMEN

## 9.1 Métricas de Tests

| Métrica | Valor |
|---------|-------|
| **Total test files** | 55 |
| **Total tests** | 303 |
| **Tests pasando** | 303 |
| **Tests fallando** | 0 |
| **Cobertura instrucciones** | 92.0% |
| **Cobertura branches** | 77.6% |
| **Cobertura líneas** | 92.0% |
| **Cobertura métodos** | 92.6% |
| **Cobertura clases** | 96.5% |

## 9.2 Tests por Capa

| Capa | Tests | % |
|------|-------|---|
| **Domain Model** | ~74 | 24.4% |
| **Domain Services / Commands** | ~21 | 6.9% |
| **Application Use Cases** | ~76 | 25.1% |
| **Infrastructure Persistence** | ~53 | 17.5% |
| **Infrastructure REST (Integración)** | ~57 | 18.8% |
| **Infrastructure Other** | ~21 | 6.9% |
| **Full Integration** | 1 | 0.3% |

## 9.3 Tests por Tipo

| Tipo | Descripción | Tests |
|------|-------------|-------|
| **Unit (Dominio)** | Sin Spring, sin mocks externos. Pura lógica. | ~95 |
| **Unit (Use Cases)** | Mocks de puertos Mockito. Sin Spring. | ~76 |
| **Unit (Infra)** | Mocks de JPA repositories. Sin Spring. | ~65 |
| **Integration** | Spring context completo + H2 + MockMvc | ~67 |

---

# PARTE 10: HISTORIAS DE USUARIO CUBIERTAS

| HU | Feature | Estado | Use Case | Tests |
|----|---------|--------|----------|-------|
| HU-K-001 | Autenticación JWT | ✅ Completado | `AuthenticateUserUseCase`, `RegisterUserUseCase` | 26 |
| HU-K-002 | Gestión de Productos | ✅ Completado | `CreateProduct`, `UpdateProduct`, `ChangeStatus`, `GetActive` | 23 |
| HU-K-003 | Procesamiento de Pedidos | ✅ Completado | `ProcessOrder`, `GetOrderStatus`, `DeleteOrder` | 24 |
| HU-K-004 | Gestión de Tareas (Estaciones) | ✅ Completado | `GetTasksByStation`, `StartTaskPreparation`, `OrderCompletion` | 20 |
| HU-K-005 | Carga Masiva Products CSV | ✅ Completado | `BulkUploadProductsUseCase` | 26 |
| HU-K-006 | Facturación / Outbox | ✅ Completado | `RequestOrderInvoice`, `InvoicePayloadBuilder` | 7 |

---

# PARTE 11: COMANDOS

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar con reporte de cobertura JaCoCo
./gradlew test jacocoTestReport

# Ejecutar solo tests unitarios
./gradlew test -Dgroups="unit"

# Ejecutar solo tests de integración
./gradlew test -Dgroups="integration"

# Abrir reporte JaCoCo
open build/reports/jacoco/test/html/index.html

# Build completo
./gradlew build

# Docker
docker build -t foodtech .
```

---

# PARTE 12: PRINCIPIOS DE TESTING APLICADOS

## Los 7 Principios (contexto Backend)

| # | Principio | Cómo aplica en este proyecto |
|---|-----------|------------------------------|
| 1 | **Las pruebas demuestran defectos** | 303 tests detectan regresiones en dominio y API |
| 2 | **Testing exhaustivo es imposible** | Priorizamos casos de negocio críticos y happy/sad paths |
| 3 | **Testing temprano ahorra $** | TDD desde el modelo de dominio antes de la infraestructura |
| 4 | **Testing depende del contexto** | Unit para dominio, Integration para REST/DB |
| 5 | **Paradoja del pesticide** | Rotamos tests, cubrimos edge cases con BulkUpload e integración |
| 6 | **Justificar decisiones** | `@DisplayName` explica el propósito de cada test |
| 7 | **Ausencia de errores ≠ calidad** | 92% coverage + tests de negocio + tests de seguridad |

---

# PARTE 13: ORDEN PARA PRESENTACIÓN

1. **TESTING_STRATEGY.md** — "303 tests, 0 fallos, 92% coverage, 6 HUs"
2. **ProductTest.java** — "Caja blanca de dominio: 32 tests sin Spring"
3. **CreateProductUseCaseTest** — "Use case con Mockito: DuplicateProduct → excepción"
4. **SecurityIntegrationTest** — "Validar: sin JWT = 401, token expirado = 401"
5. **AuthControllerIntegrationTest** — "Spring completo + H2: login real con JWT"
6. **BulkUploadProductsUseCaseTest** — "16 tests para carga masiva CSV"
7. **Correr tests:** `./gradlew test`
8. **Mostrar JaCoCo:** `build/reports/jacoco/test/html/index.html`
