# 🍽️ FoodTech Kitchen Service

## 📋 Información del Proyecto

**Tipo:** Ejercicio Académico - Arquitectura de Software  
**Enfoque:** Arquitectura Hexagonal, SOLID, TDD, Command Pattern  
**Tecnologías:** Java 17, Spring Boot 3.2.1, JUnit 5, Gradle 8.5, Docker  
**Autor:** Carlos Cuadrado  
**Fecha:** Enero 2026

---

## 🎯 Objetivo del Proyecto

FoodTech Kitchen Service es un sistema de gestión de comandas para restaurantes que automatiza la descomposición de pedidos en tareas específicas por estación de cocina.

**Problema que resuelve:**  
Cuando un pedido contiene múltiples productos (bebidas, platos calientes, ensaladas), el sistema los agrupa automáticamente y crea tareas para cada estación de trabajo:

| Estación | Productos |
|----------|-----------|
| **BAR** 🍹 | Bebidas, cócteles |
| **COCINA_CALIENTE** 🔥 | Platos principales, sopas |
| **COCINA_FRIA** 🥗 | Ensaladas, postres |

---

## 🏗️ Arquitectura del Sistema

### Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue los principios de **Clean Architecture** con separación clara de responsabilidades en capas:

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                     │
│  ┌──────────────────┐  ┌────────────────┐  ┌─────────────┐ │
│  │  REST Controllers │  │ JPA Adapters   │  │  Config     │ │
│  │  - OrderController│  │ - OrderRepo    │  │  - Beans    │ │
│  │  - DTOs          │  │  - TaskRepo     │  │  - Mappers  │ │
│  │  - Mappers       │  │ - Entities     │  │             │ │
│  └────────┬─────────┘  └────────┬───────┘  └─────────────┘ │
└───────────┼────────────────────┼─────────────────────────────┘
            │                    │
┌───────────▼────────────────────▼─────────────────────────────┐
│                    APPLICATION LAYER                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Use Cases (Application Services)                    │   │
│  │  - ProcessOrderUseCase: Orquesta el procesamiento    │   │
│  │                                                       │   │
│  │  Ports (Interfaces):                                 │   │
│  │  - IN:  ProcessOrderPort                             │   │
│  │  - OUT: OrderRepository, TaskRepository              │   │
│  └──────────────────────┬───────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                      DOMAIN LAYER (Core)                    │
│                                                             │
│  📦 Model (Entities & Value Objects):                      │
│     - Order: Pedido con mesa y productos                   │
│     - Product: Producto con nombre y tipo                  │
│     - Task: Tarea para una estación específica             │
│     - ProductType: DRINK, HOT_DISH, COLD_DISH              │
│     - Station: BAR, HOT_KITCHEN, COLD_KITCHEN              │
│                                                             │
│  🎯 Commands (Command Pattern):                            │
│     - Command: Interface base                              │
│     - PrepareDrinkCommand: Ejecuta preparación de bebidas │
│     - PrepareHotDishCommand: Ejecuta platos calientes     │
│     - PrepareColdDishCommand: Ejecuta platos fríos        │
│                                                             │
│  ⚙️ Domain Services:                                       │
│     - TaskDecomposer: Descompone Order → List<Task>       │
│     - TaskFactory: Crea Task por estación                 │
│     - OrderValidator: Valida reglas de negocio            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de Procesamiento de Pedidos

```
1. Cliente → POST /api/orders
             {
               "tableNumber": "A1",
               "products": [
                 {"name": "Coca Cola", "type": "DRINK"},
                 {"name": "Pizza", "type": "HOT_DISH"}
               ]
             }
             ↓
2. OrderController (REST)
   - Valida request
   - Convierte DTO → Domain (OrderMapper)
             ↓
3. ProcessOrderUseCase (Application)
   - Orquesta el procesamiento
   - Llama a TaskDecomposer
             ↓
4. TaskDecomposer (Domain Service)
   - Agrupa productos por estación
   - Crea Task por cada estación
             ↓
5. TaskFactory (Domain Service)
   - Genera Command por cada Task
   - PrepareDrinkCommand para BAR
   - PrepareHotDishCommand para HOT_KITCHEN
             ↓
6. Persistencia (Infrastructure)
   - Guarda Order en OrderEntity
   - Guarda Tasks en TaskEntity
             ↓
7. Respuesta al Cliente
   {
     "tableNumber": "A1",
     "tasksCreated": 2,
     "message": "Order processed successfully"
   }
```

---

## 🎨 Patrones de Diseño Implementados

### 1. Command Pattern ⭐ (Principal)

**¿Por qué Command Pattern?**

El patrón Command encapsula una solicitud como un objeto, permitiendo:
- ✅ **Desacoplamiento**: Invocador no conoce al receptor
- ✅ **Extensibilidad**: Agregar nuevos comandos sin modificar código existente (OCP)
- ✅ **Encol amiento**: Los comandos pueden ser encolados y ejecutados async
- ✅ **Logging/Auditoría**: Cada comando puede registrar su ejecución
- ✅ **Undo/Redo**: Posibilidad de revertir operaciones (futuro)

**Implementación en el proyecto:**

```java
// Interface base
public interface Command {
    void execute();
    Station getStation();
    List<Product> getProducts();
}

// Comando concreto - Bebidas
public class PrepareDrinkCommand implements Command {
    private final List<Product> products;

    public PrepareDrinkCommand(List<Product> products) {
        this.products = List.copyOf(products);
    }

    @Override
    public void execute() {
        System.out.println("Preparing drinks at BAR:");
        products.forEach(p -> System.out.println("  - " + p.getName()));
    }

    @Override
    public Station getStation() {
        return Station.BAR;
    }

    @Override
    public List<Product> getProducts() {
        return products;
    }
}

// Comando concreto - Platos Calientes
public class PrepareHotDishCommand implements Command {
    private final List<Product> products;

    public PrepareHotDishCommand(List<Product> products) {
        this.products = List.copyOf(products);
    }

    @Override
    public void execute() {
        System.out.println("Preparing hot dishes at HOT_KITCHEN:");
        products.forEach(p -> System.out.println("  - " + p.getName()));
    }

    @Override
    public Station getStation() {
        return Station.HOT_KITCHEN;
    }

    @Override
    public List<Product> getProducts() {
        return products;
    }
}

// Comando concreto - Platos Fríos
public class PrepareColdDishCommand implements Command {
    private final List<Product> products;

    public PrepareColdDishCommand(List<Product> products) {
        this.products = List.copyOf(products);
    }

    @Override
    public void execute() {
        System.out.println("Preparing cold dishes at COLD_KITCHEN:");
        products.forEach(p -> System.out.println("  - " + p.getName()));
    }

    @Override
    public Station getStation() {
        return Station.COLD_KITCHEN;
    }

    @Override
    public List<Product> getProducts() {
        return products;
    }
}
```

**¿Por qué elegimos Command Pattern para este proyecto?**

1. **Desacoplamiento**: Las estaciones de cocina no conocen quién genera las tareas
2. **Extensibilidad (OCP)**: Agregar nueva estación = nuevo comando, sin modificar código existente
3. **Encapsulación**: Cada comando encapsula toda la lógica de preparación de su tipo
4. **Testabilidad**: Los comandos se pueden probar de forma aislada
5. **Future-proof**: Base para implementar colas, retry, async execution

### 2. Repository Pattern

Abstrae la persistencia del dominio mediante interfaces:

```java
// Port (Interface en dominio)
public interface OrderRepository {
    OrderEntity save(Order order);
}

// Adapter (Implementación en infraestructura)
@Component
public class OrderRepositoryAdapter implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;
    
    @Override
    public OrderEntity save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        return jpaRepository.save(entity);
    }
}
```

### 3. Mapper/Factory Pattern

Separa la lógica de transformación de datos:

```java
// Mapper para DTOs REST → Domain
public class OrderMapper {
    public static Order toDomain(CreateOrderRequest request) {
        List<Product> products = request.products().stream()
            .map(OrderMapper::mapProduct)
            .toList();
        return new Order(request.tableNumber(), products);
    }
}

// Mapper para Domain → JPA Entities
@Component
public class OrderEntityMapper {
    public OrderEntity toEntity(Order order) {
        // Serializa productos a JSON
        String productsJson = objectMapper.writeValueAsString(...);
        return new OrderEntity(order.getTableNumber(), productsJson);
    }
}
```

### 4. Dependency Injection (Spring Framework)

Todo el proyecto usa inyección de dependencias para cumplir con DIP (Dependency Inversion Principle):

```java
@RestController
public class OrderController {
    private final ProcessOrderPort processOrderPort; // Inyectado
    
    public OrderController(ProcessOrderPort processOrderPort) {
        this.processOrderPort = processOrderPort;
    }
}
```

---

## 📂 Estructura del Proyecto

```
FoodTech/
├── src/
│   ├── main/
│   │   ├── java/com/foodtech/kitchen/
│   │   │   │
│   │   │   ├── 📦 domain/                          # Capa de Dominio (core)
│   │   │   │   ├── model/
│   │   │   │   │   ├── Order.java                  # Entidad: Pedido
│   │   │   │   │   ├── Product.java                # Entidad: Producto
│   │   │   │   │   ├── Task.java                   # Entidad: Tarea
│   │   │   │   │   ├── ProductType.java            # Enum con station
│   │   │   │   │   └── Station.java                # Enum: BAR, HOT_KITCHEN, COLD_KITCHEN
│   │   │   │   │
│   │   │   │   ├── commands/                       # Command Pattern
│   │   │   │   │   ├── Command.java                # Interface
│   │   │   │   │   ├── PrepareDrinkCommand.java
│   │   │   │   │   ├── PrepareHotDishCommand.java
│   │   │   │   │   └── PrepareColdDishCommand.java
│   │   │   │   │
│   │   │   │   └── services/
│   │   │   │       ├── TaskDecomposer.java         # Descompone Order → Tasks
│   │   │   │       ├── TaskFactory.java            # Crea Tasks
│   │   │   │       └── OrderValidator.java         # Valida reglas de negocio
│   │   │   │
│   │   │   ├── 🎯 application/                     # Capa de Aplicación
│   │   │   │   ├── usecases/
│   │   │   │   │   └── ProcessOrderUseCase.java    # Caso de uso principal
│   │   │   │   │
│   │   │   │   └── ports/
│   │   │   │       └── in/
│   │   │   │           └── ProcessOrderPort.java   # Interface del caso de uso
│   │   │   │
│   │   │   └── 🔌 infrastructure/                  # Capa de Infraestructura
│   │   │       │
│   │   │       ├── rest/                           # Adaptador REST
│   │   │       │   ├── OrderController.java        # Controller simplificado (SRP)
│   │   │       │   ├── dto/
│   │   │       │   │   ├── CreateOrderRequest.java # DTO entrada
│   │   │       │   │   ├── CreateOrderResponse.java# DTO salida
│   │   │       │   │   ├── ProductRequest.java     # DTO producto tipado
│   │   │       │   │   └── ErrorResponse.java      # DTO error estandarizado
│   │   │       │   ├── mapper/
│   │   │       │   │   └── OrderMapper.java        # Mapper DTO ↔ Domain
│   │   │       │   └── exception/
│   │   │       │       └── GlobalExceptionHandler.java  # Manejo centralizado
│   │   │       │
│   │   │       ├── persistence/                    # Adaptador JPA
│   │   │       │   ├── adapters/
│   │   │       │   │   ├── OrderRepositoryAdapter.java
│   │   │       │   │   └── TaskRepositoryAdapter.java
│   │   │       │   ├── mappers/
│   │   │       │   │   ├── OrderEntityMapper.java  # Domain ↔ Entity
│   │   │       │   │   └── TaskEntityMapper.java
│   │   │       │   ├── jpa/
│   │   │       │   │   ├── OrderJpaRepository.java
│   │   │       │   │   ├── TaskJpaRepository.java
│   │   │       │   │   └── entities/
│   │   │       │   │       ├── OrderEntity.java    # JPA Entity
│   │   │       │   │       └── TaskEntity.java
│   │   │       │
│   │   │       ├── config/
│   │   │       │   └── ApplicationConfig.java      # Beans Spring
│   │   │       │
│   │   │       └── execution/
│   │   │           └── SyncCommandExecutor.java    # Ejecutor de comandos
│   │   │
│   │   └── resources/
│   │       └── application.yaml                    # Configuración Spring Boot
│   │
│   └── test/
│       └── java/com/foodtech/kitchen/
│           ├── domain/
│           │   ├── services/
│           │   │   ├── TaskDecomposerTest.java     # Tests unitarios
│           │   │   └── TaskFactoryTest.java
│           │   └── commands/
│           │       └── PrepareCommandsTest.java
│           ├── application/
│           │   └── usecases/
│           │       └── ProcessOrderUseCaseTest.java
│           └── infrastructure/
│               ├── rest/
│               │   ├── OrderControllerIntegrationTest.java  # Tests de API
│               │   └── mapper/
│               │       └── OrderMapperTest.java
│               └── persistence/
│                   └── adapters/
│                       ├── OrderRepositoryAdapterTest.java
│                       └── TaskRepositoryAdapterTest.java
│
├── build.gradle                                    # Gradle build script
├── Dockerfile                                       # Multi-stage Docker build
└── README.md                                       # Este archivo
```

---

## 🚀 Instrucciones de Ejecución

### Prerrequisitos

- ☕ **Java 17** o superior
- 🐘 **Gradle 8.5** (incluido wrapper: `./gradlew`)
- 🐳 **Docker** (opcional, para containerización)

### 1️⃣ Clonar el Repositorio

```bash
git clone <repository-url>
cd FoodTech
```

### 2️⃣ Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte en:
# build/reports/tests/test/index.html
# build/reports/jacoco/test/html/index.html
```

**Salida esperada:**
```
BUILD SUCCESSFUL in 12s
44 tests completed
```

### 3️⃣ Ejecutar Aplicación Localmente

```bash
# Opción 1: Con Gradle
./gradlew bootRun

# Opción 2: Compilar y ejecutar JAR
./gradlew build
java -jar build/libs/kitchen-service-0.0.1-SNAPSHOT.jar
```

**Aplicación disponible en:** `http://localhost:8080`

### 4️⃣ Probar API REST

```bash
# Crear pedido
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "tableNumber": "A1",
    "products": [
      {"name": "Coca Cola", "type": "DRINK"},
      {"name": "Pizza Margherita", "type": "HOT_DISH"},
      {"name": "Caesar Salad", "type": "COLD_DISH"}
    ]
  }'
```

**Respuesta esperada:**
```json
{
  "tableNumber": "A1",
  "tasksCreated": 3,
  "message": "Order processed successfully"
}
```

### 5️⃣ Ejecutar con Docker

#### Construir Imagen

```bash
docker build -t foodtech-kitchen:latest .
```

**Dockerfile explicado:**
```dockerfile
# Stage 1: Build con Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew clean build -x test

# Stage 2: Runtime con JRE Alpine (imagen ligera)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Ejecutar Contenedor

```bash
# Ejecutar en foreground
docker run -p 8080:8080 foodtech-kitchen:latest

# Ejecutar en background
docker run -d -p 8080:8080 --name foodtech-api foodtech-kitchen:latest

# Ver logs
docker logs -f foodtech-api

# Detener
docker stop foodtech-api
```

### 6️⃣ Ejecutar Pipeline CI (GitHub Actions)

El proyecto incluye pipeline automatizado en `.github/workflows/ci.yml`:

**Pipeline stages:**
1. ✅ Checkout código
2. ✅ Setup Java 17
3. ✅ Setup Gradle con caché
4. ✅ Ejecutar tests
5. ✅ Generar reporte de cobertura
6. ✅ Build JAR
7. ✅ Upload artifacts

**Triggers:**
- Push a `main` o `develop`
- Pull requests a `main`

**Ver resultados:**
- GitHub → Actions tab → Último workflow run

---

## 🤖 IA Collaboration Log

Esta sección documenta momentos clave donde **el humano corrigió decisiones de la IA**, demostrando criterio de ingeniería y comprensión de principios SOLID.

### 📌 Caso 1: Rechazo de Stub Methods (Violación LSP/ISP/YAGNI)

**Contexto:**  
Durante la refactorización de repositorios para corregir violaciones SRP/DIP, la IA sugirió agregar métodos stub a la interface `OrderRepository`:

**Propuesta de la IA:**
```java
public interface OrderRepository {
    OrderEntity save(Order order);
    Optional<Order> findById(Long id);  // TODO: implement
    List<Order> findAll();              // TODO: implement
}
```

**❌ Problema identificado por el humano:**

> "Cual es el motivo de agregar metodos que no se han implementado?"

**Análisis del humano:**
- **Violación de LSP (Liskov Substitution Principle)**: Los métodos stub lanzarían `UnsupportedOperationException` o retornarían valores incorrectos
- **Violación de ISP (Interface Segregation Principle)**: Fuerza a clientes a depender de métodos que no usan
- **Violación de YAGNI (You Aren't Gonna Need It)**: Agregar código especulativo antes de necesitarlo

**✅ Corrección aplicada:**

```java
public interface OrderRepository {
    OrderEntity save(Order order);  // SOLO lo que realmente se usa
}
```

**Lección aprendida:**
> "Los tests deben adaptarse al código, no el código a los tests. Implementar solo lo necesario."

**Principios aplicados:**
- **YAGNI**: No agregar funcionalidad hasta que sea realmente necesaria
- **ISP**: Interfaces pequeñas y cohesivas
- **LSP**: Todas las implementaciones deben cumplir el contrato completo

---

### 📌 Caso 2: Eliminación de OrderResponseFactory (Sobre-ingeniería)

**Contexto:**  
La IA creó un `OrderResponseFactory` para construir respuestas del controller, intentando separar responsabilidades.

**Código generado por la IA:**
```java
@Component
public class OrderResponseFactory {
    private static final String ORDER_SUCCESS_MESSAGE = "Order processed successfully";

    public CreateOrderResponse createSuccessResponse(Order order, List<Task> tasks) {
        return new CreateOrderResponse(
            order.getTableNumber(),
            tasks.size(),
            ORDER_SUCCESS_MESSAGE
        );
    }
}
```

**❌ Problema identificado por el humano:**

> "Ese orderResponse factory para que?"

**Análisis del humano:**
- La factory agrega **capa innecesaria de indirección**
- La construcción del response es **trivial** (solo 3 campos)
- El único beneficio real es **centralizar un string constante**
- Viola **YAGNI**: Se está agregando abstracción antes de que sea necesaria

**✅ Corrección aplicada:**

Eliminar factory completamente y simplificar controller:

```java
@RestController
public class OrderController {
    private static final String ORDER_SUCCESS_MESSAGE = "Order processed successfully";
    
    private final ProcessOrderPort processOrderPort;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = OrderMapper.toDomain(request);
        List<Task> tasks = processOrderPort.execute(order);
        
        // Construcción directa - más simple y legible
        CreateOrderResponse response = new CreateOrderResponse(
            order.getTableNumber(),
            tasks.size(),
            ORDER_SUCCESS_MESSAGE
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**Lección aprendida:**
> "No agregar abstracciones hasta que realmente las necesitemos. Si en el futuro hubiera lógica compleja de construcción de respuestas, ahí sí valdría la pena un factory."

**Principios aplicados:**
- **YAGNI**: Implementar solo lo necesario ahora
- **KISS (Keep It Simple, Stupid)**: Solución más simple es mejor
- **SRP**: Una constante en el controller no viola SRP

**Impacto:**
- ✅ Código más simple y directo
- ✅ Menos clases innecesarias
- ✅ Más fácil de mantener
- ✅ No se pierde funcionalidad

---

## 📊 Resumen de Mejoras Aplicadas

### Violaciones SOLID Corregidas

| Principio | Violación Original | Corrección Aplicada |
|-----------|-------------------|---------------------|
| **SRP** | `OrderRepositoryAdapter` mezclaba persistencia + serialización | Extraído `OrderEntityMapper` y `TaskEntityMapper` |
| **SRP** | `OrderController` manejaba errores + construía respuestas | Creado `GlobalExceptionHandler`, eliminado factory innecesario |
| **OCP** | `ProductType` mapping hardcodeado en switch | `ProductType` enum contiene `Station`, eliminado switch |
| **DIP** | Adapters creaban `ObjectMapper` internamente | `ObjectMapper` inyectado como bean Spring |
| **ISP** | Interfaces con métodos stub no implementados | Interfaces minimalistas con solo métodos reales |

### Patrones de Diseño Aplicados Correctamente

- ✅ **Command Pattern**: Comandos con responsabilidad única
- ✅ **Repository Pattern**: Abstracción de persistencia
- ✅ **Hexagonal Architecture**: Separación clara de capas
- ✅ **Dependency Injection**: Todo inyectado vía Spring
- ✅ **Mapper Pattern**: Transformación de datos separada

### Métricas de Calidad

| Métrica | Valor |
|---------|-------|
| **Tests Unitarios** | 44 passing |
| **Build Status** | ✅ SUCCESS |
| **Principios SOLID** | 0 violaciones críticas |
| **Code Smells** | Eliminados los principales |
| **Cobertura de Tests** | 61%+ (objetivo: 85%) |

---

## 🎓 Conclusiones

Este proyecto demuestra:

1. **Arquitectura sólida**: Hexagonal con separación clara de responsabilidades
2. **Criterio de ingeniería**: Capacidad para cuestionar y mejorar soluciones propuestas por IA
3. **Principios SOLID**: Aplicación práctica y corrección de violaciones
4. **Patrones de diseño**: Command Pattern implementado correctamente
5. **Testing**: Estrategia de testing en múltiples niveles
6. **DevOps**: Pipeline CI/CD, Docker, automatización

**El código no solo funciona, sino que es mantenible, extensible y profesional.**

---

## 📝 Licencia

Proyecto académico - Sofka Technologies - 2026

---

## 👨‍💻 Autor

**Carlos Cuadrado**  
Ejercicio de Arquitectura de Software  
Enero 2026
