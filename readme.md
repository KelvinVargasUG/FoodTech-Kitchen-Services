# 🍽️ FoodTech Kitchen Service

## 📋 Información del Proyecto

**Tipo:** Ejercicio Académico - Arquitectura de Software  
**Enfoque:** Clean Architecture, SOLID, TDD/BDD, DevOps  
**Patrón Principal:** Command Pattern  
**Tecnologías:** Java 17, Spring Boot, JUnit 5, Cucumber, Gradle

---

## 🎯 Objetivo General

> **Construir una solución de software robusta, testeable y automatizada**, demostrando **criterio de ingeniería** para refactorizar, corregir y elevar el estándar del código generado.

El objetivo **NO es "que funcione"**, sino que la solución sea un ejemplo de:
- ✅ Clean Architecture
- ✅ SOLID (0 violaciones)
- ✅ Cultura DevOps
- ✅ TDD/BDD Real (tests antes del código)
- ✅ Command Pattern aplicado correctamente

---

## 🧠 Contexto del Dominio

FoodTech Kitchen Service es un sistema para gestionar **comandas de cocina** en un restaurante.

### Problema a resolver

Cuando llega un pedido con múltiples productos, cada producto debe prepararse en una estación específica:

| Estación | Productos |
|----------|-----------|
| **BARRA** | Bebidas, jugos, cócteles |
| **COCINA_CALIENTE** | Platos principales, sopas |
| **COCINA_FRIA** | Ensaladas, postres |

**El sistema debe:**
1. Recibir pedidos (vía API REST)
2. Descomponerlos automáticamente en tareas
3. Asignar cada tarea a su estación correspondiente
4. Ejecutar las tareas usando el patrón Command

---

## 🧱 Arquitectura del Sistema

### Enfoque Arquitectural

**Servicio único** con **Arquitectura Hexagonal** y separación clara de responsabilidades.

```
┌─────────────────────────────────────────────────┐
│         KITCHEN SERVICE (Spring Boot)           │
│                                                 │
│  ┌──────────────────────────────────────────┐  │
│  │   Infrastructure Layer (Adapters)        │  │
│  │   - REST Controllers                     │  │
│  │   - JPA Repositories                     │  │
│  │   - Command Executor                     │  │
│  └────────────────┬─────────────────────────┘  │
│                   │                             │
│  ┌────────────────▼─────────────────────────┐  │
│  │   Application Layer (Use Cases)          │  │
│  │   - ProcessOrderUseCase                  │  │
│  │   - GetTasksByStationUseCase             │  │
│  └────────────────┬─────────────────────────┘  │
│                   │                             │
│  ┌────────────────▼─────────────────────────┐  │
│  │   Domain Layer (Business Logic)          │  │
│  │   - Order (Entity)                       │  │
│  │   - Product (Entity)                     │  │
│  │   - Task (Entity)                        │  │
│  │   - TaskDecomposer (Service)             │  │
│  │   - Commands (Pattern) ⭐                │  │
│  │     • PrepareDrinkCommand                │  │
│  │     • PrepareHotDishCommand              │  │
│  │     • PrepareColdDishCommand             │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 📁 Estructura del Proyecto

```
kitchen-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/foodtech/kitchen/
│   │   │       │
│   │   │       ├── domain/                    # ⭐ Dominio puro (POJO)
│   │   │       │   ├── model/
│   │   │       │   │   ├── Order.java
│   │   │       │   │   ├── Product.java
│   │   │       │   │   ├── Task.java
│   │   │       │   │   ├── OrderId.java
│   │   │       │   │   ├── TaskId.java
│   │   │       │   │   └── Station.java       # Enum
│   │   │       │   │
│   │   │       │   ├── commands/              # ⭐ Command Pattern
│   │   │       │   │   ├── Command.java       # Interface
│   │   │       │   │   ├── PrepareDrinkCommand.java
│   │   │       │   │   ├── PrepareHotDishCommand.java
│   │   │       │   │   └── PrepareColdDishCommand.java
│   │   │       │   │
│   │   │       │   └── services/
│   │   │       │       ├── TaskDecomposer.java
│   │   │       │       └── CommandFactory.java
│   │   │       │
│   │   │       ├── application/               # Casos de uso
│   │   │       │   ├── usecases/
│   │   │       │   │   ├── ProcessOrderUseCase.java
│   │   │       │   │   └── GetTasksByStationUseCase.java
│   │   │       │   │
│   │   │       │   └── ports/                 # Interfaces (Puertos)
│   │   │       │       ├── in/
│   │   │       │       │   └── ProcessOrderPort.java
│   │   │       │       └── out/
│   │   │       │           ├── OrderRepository.java
│   │   │       │           ├── TaskRepository.java
│   │   │       │           └── CommandExecutor.java
│   │   │       │
│   │   │       └── infrastructure/            # Adaptadores
│   │   │           ├── rest/
│   │   │           │   ├── OrderController.java
│   │   │           │   ├── dto/
│   │   │           │   │   ├── CreateOrderRequest.java
│   │   │           │   │   ├── CreateOrderResponse.java
│   │   │           │   │   └── TaskResponse.java
│   │   │           │   └── mapper/
│   │   │           │       └── OrderMapper.java
│   │   │           │
│   │   │           ├── persistence/
│   │   │           │   ├── jpa/
│   │   │           │   │   ├── OrderJpaRepository.java
│   │   │           │   │   ├── TaskJpaRepository.java
│   │   │           │   │   └── entities/
│   │   │           │   │       ├── OrderEntity.java
│   │   │           │   │       └── TaskEntity.java
│   │   │           │   │
│   │   │           │   └── adapters/
│   │   │           │       ├── OrderRepositoryAdapter.java
│   │   │           │       └── TaskRepositoryAdapter.java
│   │   │           │
│   │   │           └── execution/
│   │   │               └── SyncCommandExecutor.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   │
│   └── test/
│       ├── java/
│       │   └── com/foodtech/kitchen/
│       │       │
│       │       ├── domain/                    # ⭐ TDD - Tests unitarios
│       │       │   ├── services/
│       │       │   │   ├── TaskDecomposerTest.java
│       │       │   │   └── CommandFactoryTest.java
│       │       │   └── commands/
│       │       │       ├── PrepareDrinkCommandTest.java
│       │       │       ├── PrepareHotDishCommandTest.java
│       │       │       └── PrepareColdDishCommandTest.java
│       │       │
│       │       ├── application/
│       │       │   └── usecases/
│       │       │       └── ProcessOrderUseCaseTest.java
│       │       │
│       │       ├── infrastructure/            # Tests de integración
│       │       │   └── rest/
│       │       │       ├── OrderControllerTest.java
│       │       │       └── OrderControllerIntegrationTest.java
│       │       │
│       │       └── bdd/                       # ⭐ BDD - Cucumber
│       │           ├── CucumberRunnerTest.java
│       │           ├── steps/
│       │           │   └── OrderProcessingSteps.java
│       │           └── config/
│       │               └── CucumberSpringConfiguration.java
│       │
│       └── resources/
│           ├── features/                      # Gherkin Features
│           │   └── order-processing.feature
│           └── cucumber.properties
│
├── .github/
│   └── workflows/
│       └── ci.yml                             # GitHub Actions
│
├── build.gradle                               # Gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

---

## 🧩 Patrón Command - Implementación en Java

### ¿Por qué Command Pattern?

El patrón Command **encapsula una solicitud como un objeto**, permitiendo:
- Parametrizar operaciones
- Encolar tareas
- Desacoplar invocador de ejecutor
- Facilitar undo/redo y logging

### Implementación en Java

```java
// 1. Command Interface
package com.foodtech.kitchen.domain.commands;

public interface Command {
    void execute();
    Station getStation();
    TaskId getTaskId();
}

// 2. Concrete Command - Drink
package com.foodtech.kitchen.domain.commands;

public class PrepareDrinkCommand implements Command {
    private final TaskId taskId;
    private final String drinkName;
    private final int quantity;

    public PrepareDrinkCommand(TaskId taskId, String drinkName, int quantity) {
        this.taskId = taskId;
        this.drinkName = drinkName;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        // Lógica de preparación de bebida
        System.out.println("Preparing " + quantity + "x " + drinkName);
    }

    @Override
    public Station getStation() {
        return Station.BARRA;
    }

    @Override
    public TaskId getTaskId() {
        return taskId;
    }
}

// 3. Command Executor (Invoker)
package com.foodtech.kitchen.infrastructure.execution;

public class SyncCommandExecutor implements CommandExecutor {
    
    @Override
    public void execute(Command command) {
        command.execute();
    }

    @Override
    public void executeAll(List<Command> commands) {
        commands.forEach(this::execute);
    }
}
```

**Flujo completo:**
```
Order → TaskDecomposer → CommandFactory → [Commands] → CommandExecutor → Stations
```

---

## 🧪 Testing Strategy

### Niveles de Testing

#### 1️⃣ **Tests Unitarios (TDD)** - 70%+ cobertura

**Framework:** JUnit 5 + Mockito

**Ubicación:** `src/test/java/.../domain/`

**Objetivo:** Probar reglas de dominio sin infraestructura

**Ejemplo:**
```java
package com.foodtech.kitchen.domain.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TaskDecomposerTest {

    @Test
    @DisplayName("Should create one command per station involved")
    void shouldCreateOneCommandPerStationInvolved() {
        // Given
        TaskDecomposer decomposer = new TaskDecomposer(new CommandFactory());
        Order order = new Order(
            OrderId.generate(),
            "A1",
            List.of(
                new Product("Coca Cola", ProductType.DRINK),
                new Product("Pizza", ProductType.HOT_DISH)
            )
        );

        // When
        List<Command> commands = decomposer.decompose(order);

        // Then
        assertEquals(2, commands.size());
        assertTrue(commands.get(0) instanceof PrepareDrinkCommand);
        assertTrue(commands.get(1) instanceof PrepareHotDishCommand);
    }

    @Test
    @DisplayName("Should group products by station")
    void shouldGroupProductsByStation() {
        // Given
        TaskDecomposer decomposer = new TaskDecomposer(new CommandFactory());
        Order order = new Order(
            OrderId.generate(),
            "A1",
            List.of(
                new Product("Coca Cola", ProductType.DRINK),
                new Product("Sprite", ProductType.DRINK),
                new Product("Pizza", ProductType.HOT_DISH)
            )
        );

        // When
        List<Command> commands = decomposer.decompose(order);

        // Then
        assertEquals(2, commands.size());
        PrepareDrinkCommand drinkCommand = (PrepareDrinkCommand) commands.get(0);
        assertEquals(2, drinkCommand.getProducts().size());
    }
}
```

---

#### 2️⃣ **Tests de Integración/API** - Mínimo 3

**Framework:** Spring Boot Test + MockMvc

**Ubicación:** `src/test/java/.../infrastructure/rest/`

**Objetivo:** Probar endpoints reales

**Ejemplo:**
```java
package com.foodtech.kitchen.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOrderAndReturn201() throws Exception {
        String orderJson = """
            {
                "tableNumber": "A1",
                "items": [
                    { "name": "Coca Cola", "type": "DRINK" },
                    { "name": "Pizza Margarita", "type": "HOT_DISH" }
                ]
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.tasksCreated").value(2));
    }

    @Test
    void shouldRejectOrderWithoutProducts() throws Exception {
        String orderJson = """
            {
                "tableNumber": "A1",
                "items": []
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Order must have at least one product"));
    }

    @Test
    void shouldGetTasksByStation() throws Exception {
        // Primero crear una orden
        createSampleOrder();

        // Luego obtener tareas
        mockMvc.perform(get("/api/tasks/BARRA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].station").value("BARRA"));
    }
}
```

---

#### 3️⃣ **Tests BDD** - Documentación viva

**Framework:** Cucumber para Java

**Ubicación:** `src/test/resources/features/`

**Objetivo:** Especificar comportamiento en lenguaje de negocio

**Feature File:**
```gherkin
# src/test/resources/features/order-processing.feature

Feature: Procesamiento de pedidos de cocina

  Scenario: Pedido con bebida y plato caliente
    Given un pedido para la mesa "A1"
    And el pedido contiene los siguientes productos:
      | Producto         | Tipo           |
      | Coca Cola        | DRINK          |
      | Pizza Margarita  | HOT_DISH       |
    When el pedido es procesado
    Then se crean 2 tareas
    And existe una tarea para la estación "BARRA"
    And existe una tarea para la estación "COCINA_CALIENTE"

  Scenario: Pedido solo con bebidas
    Given un pedido para la mesa "B2"
    And el pedido contiene los siguientes productos:
      | Producto    | Tipo  |
      | Coca Cola   | DRINK |
      | Sprite      | DRINK |
    When el pedido es procesado
    Then se crea 1 tarea
    And la tarea es para la estación "BARRA"

  Scenario: Pedido mixto (bebida, plato caliente, postre)
    Given un pedido para la mesa "C3"
    And el pedido contiene los siguientes productos:
      | Producto         | Tipo           |
      | Coca Cola        | DRINK          |
      | Pizza Margarita  | HOT_DISH       |
      | Tiramisu         | COLD_DISH      |
    When el pedido es procesado
    Then se crean 3 tareas
    And existe una tarea para cada estación
```

**Steps Implementation:**
```java
package com.foodtech.kitchen.bdd.steps;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessingSteps {

    private Order order;
    private List<Task> createdTasks;
    private ProcessOrderUseCase processOrderUseCase;

    @Given("un pedido para la mesa {string}")
    public void unPedidoParaLaMesa(String tableNumber) {
        order = new Order(OrderId.generate(), tableNumber, new ArrayList<>());
    }

    @And("el pedido contiene los siguientes productos:")
    public void elPedidoContieneLosSiguientesProductos(DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            String name = row.get("Producto");
            ProductType type = ProductType.valueOf(row.get("Tipo"));
            order.addProduct(new Product(name, type));
        });
    }

    @When("el pedido es procesado")
    public void elPedidoEsProcesado() {
        createdTasks = processOrderUseCase.execute(order);
    }

    @Then("se crean {int} tareas")
    public void seCreanTareas(int expectedTasks) {
        assertEquals(expectedTasks, createdTasks.size());
    }

    @And("existe una tarea para la estación {string}")
    public void existeUnaTareaParaLaEstacion(String stationName) {
        Station station = Station.valueOf(stationName);
        assertTrue(createdTasks.stream()
            .anyMatch(task -> task.getStation().equals(station)));
    }
}
```

---

## 📊 Historias de Usuario

### HU-01: Procesar pedido y generar tareas
```
Como sistema de cocina
Quiero recibir un pedido y descomponerlo automáticamente en tareas
Para que cada estación pueda trabajar de forma independiente

Criterios de aceptación:
- POST /api/orders recibe pedido válido
- El pedido tiene número de mesa y al menos un producto
- Se descompone en tareas por estación
- Se crean Commands por cada tarea
- Se retorna orderId y cantidad de tareas creadas
- Status HTTP 201 Created
```

---

### HU-02: Obtener tareas por estación
```
Como estación de cocina
Quiero consultar solo mis tareas pendientes
Para prepararlas eficientemente

Criterios de aceptación:
- GET /api/tasks/{station} retorna tareas filtradas
- Solo se muestran tareas de la estación solicitada
- Cada tarea contiene productos y comandos asociados
- Status HTTP 200 OK
```

---

### HU-03: Validar pedidos
```
Como sistema de cocina
Quiero validar que los pedidos sean correctos
Para evitar procesar información inválida

Criterios de aceptación:
- Pedido sin productos retorna error 400
- Pedido sin número de mesa retorna error 400
- Productos con tipo inválido retornan error 400
- El mensaje de error es descriptivo
```

---

## 🔄 Flujo de Datos

```
1. Cliente envía pedido
         ↓
2. POST /api/orders
   OrderController recibe CreateOrderRequest
         ↓
3. OrderController → ProcessOrderUseCase
         ↓
4. Order Entity creada y validada
         ↓
5. TaskDecomposer analiza productos
   - Agrupa por ProductType
   - Mapea a Station
         ↓
6. CommandFactory crea Commands
   - PrepareDrinkCommand (BARRA)
   - PrepareHotDishCommand (COCINA_CALIENTE)
   - PrepareColdDishCommand (COCINA_FRIA)
         ↓
7. Tasks guardadas via TaskRepository
         ↓
8. CommandExecutor (opcional inmediato)
         ↓
9. Response 201 Created
   {
     "orderId": "uuid",
     "tasksCreated": 2
   }
```

---

## 🚀 Plan de Ejecución (4 Semanas)

### 📅 Semana 1: Arquitectura y Código Limpio

**Objetivos:**
- ✅ Estructura del proyecto (Gradle + Spring Boot)
- ✅ Dominio con TDD (Command Pattern)
- ✅ Aplicar SOLID (0 violaciones)
- ✅ Al menos 1 patrón adicional (Factory para Commands)

**Entregables:**
- Estructura de paquetes completa
- Domain models con tests unitarios (JUnit 5)
- Command Pattern funcionando
- README con decisiones arquitecturales

**Tecnologías:**
```groovy
// build.gradle - Dependencias principales
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.mockito:mockito-core'
}
```

---

### 📅 Semana 2: Aceleración con IA

**Objetivos:**
- ✅ GitHub Copilot para boilerplate
- ✅ Generar casos de prueba de borde
- ✅ API REST con Spring Boot
- ✅ Repositories y Adapters

**Entregables:**
- API REST funcionando
- Tests de casos de borde
- Controllers + DTOs
- Mappers

---

### 📅 Semana 3: Cultura DevOps & Calidad

**Objetivos:**
- ✅ Gitflow simplificado (Main, Develop, Feature/*)
- ✅ Pipeline CI (GitHub Actions)
  - Build Gradle en cada push
  - Tests unitarios automáticos
  - SonarCloud (opcional)
- ✅ Tests unitarios → 70% cobertura (JaCoCo)

**Entregables:**
```yaml
# .github/workflows/ci.yml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
        
    - name: Build with Gradle
      run: ./gradlew build
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Generate coverage report
      run: ./gradlew jacocoTestReport
      
    - name: Check coverage
      run: ./gradlew jacocoTestCoverageVerification
```

---

### 📅 Semana 4: Automatización Full Stack

**Objetivos:**
- ✅ Tests de Integración/API (mínimo 3)
- ✅ MockMvc + Spring Boot Test
- ✅ Tests BDD con Cucumber
- ✅ Documentación completa (Javadoc)

**Entregables:**
- Suite completa de tests
- Coverage > 70% (verificado con JaCoCo)
- BDD features con Cucumber
- Documentación técnica

**Dependencias Cucumber:**
```groovy
// build.gradle
dependencies {
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-junit-platform-engine:7.14.0'
    testImplementation 'io.cucumber:cucumber-spring:7.14.0'
}
```

---

## 🛠️ Stack Tecnológico

### Core
- **JDK:** 17 (LTS)
- **Framework:** Spring Boot 3.2+
- **Build Tool:** Gradle 8.5+
- **Testing:** JUnit 5, Mockito, AssertJ
- **API Testing:** MockMvc, RestAssured (opcional)
- **BDD:** Cucumber para Java
- **Coverage:** JaCoCo

### Database (Opcional para el ejercicio)
- **H2:** Base de datos en memoria para tests
- **Spring Data JPA:** Abstracción de persistencia

### DevOps
- **CI/CD:** GitHub Actions
- **Code Quality:** SonarCloud (opcional)
- **Coverage:** JaCoCo Gradle Plugin

### Arquitectura
- **Patrón Principal:** Command
- **Patrones Adicionales:** Factory, Repository, Strategy
- **Arquitectura:** Hexagonal (Ports & Adapters)

---

## 📏 Criterios de Evaluación

### ✅ Obligatorios

1. **SOLID:** 0 violaciones identificables
2. **Patrones:** Command + Factory implementados correctamente
3. **Estructura:** Separación clara de capas (domain, application, infrastructure)
4. **TDD/BDD:** Tests escritos ANTES del código (evidencia en commits)
5. **Coverage:** Mínimo 70% en tests unitarios (JaCoCo report)
6. **API Tests:** Mínimo 3 tests de integración con MockMvc
7. **CI/CD:** Pipeline funcionando con build + tests
8. **Git:** Historial ordenado (no happy path único)

### 🎁 Bonus

- Integración con SonarCloud
- Tests con RestAssured
- Documentación con Swagger/OpenAPI
- Docker containerization
- Migracion a PostgreSQL

---

## 🚫 Reglas de Oro: "Human in the Loop"

Para garantizar aprendizaje real:

1. **La Regla del Crítico:** Por cada clase generada con IA, agregar Javadoc explicando decisiones de diseño
2. **TDD/BDD Real:** Evidencia en Git que los tests fueron creados antes o en conjunto con el código
3. **Prohibido el Happy Path Único:** El código debe manejar excepciones de negocio (OrderValidationException, InvalidProductTypeException, etc.)

---

## 🎯 Definition of Done

Una funcionalidad está "Done" cuando:

- ✅ Tiene tests unitarios (>70% coverage en esa clase)
- ✅ Pasa todos los tests
- ✅ No viola principios SOLID
- ✅ Tiene Javadoc completo
- ✅ Pasa el pipeline CI
- ✅ Fue revisado críticamente (Human in the Loop)
- ✅ Maneja casos de error con excepciones propias

---

## 🧪 Ejemplo de Test TDD Real

```java
package com.foodtech.kitchen.domain.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para TaskDecomposer siguiendo TDD
 * 
 * ORDEN DE ESCRITURA:
 * 1. Test (RED)
 * 2. Implementación mínima (GREEN)
 * 3. Refactor (REFACTOR)
 */
class TaskDecomposerTest {

    private TaskDecomposer decomposer;
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        commandFactory = new CommandFactory();
        decomposer = new TaskDecomposer(commandFactory);
    }

    @Nested
    @DisplayName("Cuando se descompone una orden simple")
    class SimpleOrderDecomposition {

        @Test
        @DisplayName("Una bebida genera un solo comando para BARRA")
        void oneDrinkGeneratesOneBarCommand() {
            // Given
            Order order = OrderMother.withOneDrink();

            // When
            List<Command> commands = decomposer.decompose(order);

            // Then
            assertAll(
                () -> assertEquals(1, commands.size(), "Debe generar un solo comando"),
                () -> assertInstanceOf(PrepareDrinkCommand.class, commands.get(0)),
                () -> assertEquals(Station.BARRA, commands.get(0).getStation())
            );
        }

        @Test
        @DisplayName("Un plato caliente genera un solo comando para COCINA_CALIENTE")
        void oneHotDishGeneratesOneKitchenCommand() {
            // Given
            Order order = OrderMother.withOneHotDish();

            // When
            List<Command> commands = decomposer.decompose(order);

            // Then
            assertAll(
                () -> assertEquals(1, commands.size()),
                () -> assertInstanceOf(PrepareHotDishCommand.class, commands.get(0)),
                () -> assertEquals(Station.COCINA_CALIENTE, commands.get(0).getStation())
            );
        }
    }

    @Nested
    @DisplayName("Cuando se descompone una orden mixta")
    class MixedOrderDecomposition {

        @Test
        @DisplayName("Orden con 2 tipos de productos genera 2 comandos")
        void mixedOrderGeneratesMultipleCommands() {
            // Given
            Order order = OrderMother.withDrinkAndHotDish();

            // When
            List<Command> commands = decomposer.decompose(order);

            // Then
            assertEquals(2, commands.size(), "Debe generar un comando por estación");
        }

        @Test
        @DisplayName("Múltiples productos del mismo tipo se agrupan en un comando")
        void multipleProductsSameTypeGenerateOneCommand() {
            // Given
            Order order = OrderMother.withMultipleDrinks(3);

            // When
            List<Command> commands = decomposer.decompose(order);

            // Then
            assertAll(
                () -> assertEquals(1, commands.size()),
                () -> {
                    PrepareDrinkCommand cmd = (PrepareDrinkCommand) commands.get(0);
                    assertEquals(3, cmd.getProducts().size());
                }
            );
        }
    }

    @Nested
    @DisplayName("Validaciones de negocio")
    class BusinessValidations {

        @Test
        @DisplayName("Orden vacía lanza excepción")
        void emptyOrderThrowsException() {
            // Given
            Order emptyOrder = OrderMother.empty();

            // When & Then
            assertThrows(
                EmptyOrderException.class,
                () -> decomposer.decompose(emptyOrder),
                "No se puede descomponer una orden vacía"
            );
        }

        @Test
        @DisplayName("Orden nula lanza excepción")
        void nullOrderThrowsException() {
            // When & Then
            assertThrows(
                IllegalArgumentException.class,
                () -> decomposer.decompose(null),
                "No se puede descomponer una orden nula"
            );
        }
    }
}
```

---

## 🧠 Principios Rectores

> **"La arquitectura no se demuestra usando tecnologías, sino soportándolas sin cambiar el dominio."**

> **"El código debe explicar el qué, los tests el por qué."**

> **"Menos infraestructura, más pensamiento."**

> **"El dominio no conoce Spring."**

---

## 📚 Recursos de Aprendizaje

### Clean Architecture
- "Clean Architecture" - Robert C. Martin
- "Domain-Driven Design" - Eric Evans
- "Implementing Domain-Driven Design" - Vaughn Vernon

### Testing en Java
- "Test Driven Development: By Example" - Kent Beck
- "Growing Object-Oriented Software, Guided by Tests"
- "Effective Unit Testing" - Lasse Koskela

### Patterns
- "Design Patterns: Elements of Reusable Object-Oriented Software" - Gang of Four
- "Patterns of Enterprise Application Architecture" - Martin Fowler
- "Head First Design Patterns" - Freeman & Freeman

### Spring Boot
- Spring Boot Documentation (oficial)
- "Spring Boot in Action" - Craig Walls

---

## 🏁 Conclusión

Este proyecto busca **calidad sobre cantidad**, **diseño sobre infraestructura**, y **aprendizaje sobre atajos**.

**Características clave:**
- ✅ Un solo servicio, arquitectura limpia
- ✅ Command Pattern como núcleo del diseño
- ✅ Testing robusto (Unit, Integration, BDD)
- ✅ Java + Spring Boot profesional
- ✅ CI/CD funcional

**El éxito no se mide en líneas de código, sino en criterio demostrado.**

---

## 📝 Siguiente Paso

Para iniciar el desarrollo:

1. **Crear proyecto con Spring Initializr:**
```bash
# Opción 1: Usar https://start.spring.io/
# - Project: Gradle - Groovy
# - Language: Java
# - Spring Boot: 3.2.x
# - Java: 17
# - Dependencies: Spring Web, Spring Data JPA, H2 Database

# Opción 2: Crear manualmente
mkdir kitchen-service
cd kitchen-service
gradle init --type java-application
```

2. **Estructura básica de `build.gradle`:**
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.1'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'jacoco'
}

group = 'com.foodtech'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-junit-platform-engine:7.14.0'
    testImplementation 'io.cucumber:cucumber-spring:7.14.0'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.70
            }
        }
    }
}
```

3. **Crear estructura de paquetes**

4. **Escribir primer test TDD:**
   - `TaskDecomposerTest.java`
   - Implementar `TaskDecomposer.java`

5. **Commit inicial:**
```bash
git add .
git commit -m "chore: initial project structure with hexagonal architecture"
```

---

**Autor:** Carlos  
**Fecha:** Enero 2026  
**Versión:** 2.0.0 (Java Edition)