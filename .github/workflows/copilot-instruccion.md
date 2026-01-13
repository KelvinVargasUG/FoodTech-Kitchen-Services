# 📋 Instrucciones de Desarrollo - FoodTech Kitchen Service

## 🎯 Contexto del Proyecto

Este es un **ejercicio académico** enfocado en demostrar dominio de arquitectura de software, principios SOLID, Clean Code y TDD. El objetivo NO es solo que funcione, sino que el código sea **ejemplar, mantenible y profesional**.

**Lee estos documentos ANTES de generar código:**
- `readme.md` - Arquitectura, contexto técnico y decisiones de diseño
- `HISTORIAS_DE_USUARIO.md` - Requisitos de negocio y criterios de aceptación

---

## 🏗️ Arquitectura Obligatoria

### Arquitectura Hexagonal (Ports & Adapters)
```
domain/          → Lógica de negocio pura (NO depende de frameworks)
application/     → Casos de uso (orquestación)
infrastructure/  → Adaptadores (REST, JPA, config)
```

**Reglas NO negociables:**
- ❌ Domain NUNCA importa de application o infrastructure
- ❌ Domain NUNCA usa anotaciones de Spring (@Component, @Service, etc.)
- ✅ Domain solo contiene lógica de negocio pura
- ✅ Dependencies apuntan HACIA el dominio (Dependency Inversion)

---

## ⚙️ Principios SOLID (Aplicar SIEMPRE)

### 1. Single Responsibility Principle (SRP)
- Cada clase tiene UNA responsabilidad
- Métodos cortos (máximo 15-20 líneas)
- Si una clase hace "serialización Y mapeo" → DIVIDIR

**Ejemplo:**
```java
// ❌ MAL - Viola SRP
class OrderController {
    public Response createOrder() {
        // validar
        // mapear
        // persistir
        // ejecutar comandos
        // construir respuesta
    }
}

// ✅ BIEN - Cumple SRP
class OrderController {
    private final ProcessOrderPort processOrderPort;
    
    public Response createOrder(Request request) {
        Order order = OrderMapper.toDomain(request);
        List tasks = processOrderPort.execute(order);
        return new CreateOrderResponse(order.getTableNumber(), tasks.size());
    }
}
```

### 2. Open/Closed Principle (OCP)
- Abierto para extensión, cerrado para modificación
- Usa enums con comportamiento en lugar de switches
- Usa Strategy/Command patterns

**Ejemplo:**
```java
// ❌ MAL - Requiere modificación al agregar tipos
public Station getStation(ProductType type) {
    switch(type) {
        case DRINK: return Station.BAR;
        case HOT_DISH: return Station.HOT_KITCHEN;
        // Si agrego DESSERT, debo modificar este código
    }
}

// ✅ BIEN - Extensible sin modificación
public enum ProductType {
    DRINK(Station.BAR),
    HOT_DISH(Station.HOT_KITCHEN),
    COLD_DISH(Station.COLD_KITCHEN);
    
    private final Station station;
    public Station getStation() { return station; }
}
```

### 3. Liskov Substitution Principle (LSP)
- Nunca agregues métodos stub que lancen UnsupportedOperationException
- Interfaces deben poder ser implementadas completamente

### 4. Interface Segregation Principle (ISP)
- Interfaces pequeñas y específicas
- Clientes no deben depender de métodos que no usan

### 5. Dependency Inversion Principle (DIP)
- Depende de abstracciones (interfaces), no de implementaciones
- Inyección de dependencias vía constructor

---

## 🧪 Test-Driven Development (TDD)

### Ciclo RED-GREEN-REFACTOR obligatorio:

1. **RED**: Escribe test que falla
2. **GREEN**: Escribe código mínimo para pasar test
3. **REFACTOR**: Mejora código manteniendo tests verdes

### Estrategia de Testing
```java
// Test de dominio (UNITARIO - sin frameworks)
class TaskDecomposerTest {
    @Test
    void shouldDecomposeOrderIntoTasksByStation() {
        // GIVEN
        Order order = new Order("A1", List.of(
            new Product("Coca Cola", ProductType.DRINK),
            new Product("Pizza", ProductType.HOT_DISH)
        ));
        
        // WHEN
        List tasks = taskDecomposer.decompose(order);
        
        // THEN
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getStation()).isEqualTo(Station.BAR);
    }
}

// Test de caso de uso (CON mocks de repositorios)
class ProcessOrderUseCaseTest {
    @Mock private TaskRepository taskRepository;
    @InjectMocks private ProcessOrderUseCase useCase;
    
    @Test
    void shouldSaveTasksAfterDecomposing() {
        // ...
        verify(taskRepository).saveAll(anyList());
    }
}

// Test de integración (REST controllers)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    @Test
    void shouldReturn201WhenCreatingValidOrder() {
        // ...
        mockMvc.perform(post("/api/orders")...)
               .andExpect(status().isCreated());
    }
}
```

**Nombrado de tests:**
- `should[ExpectedBehavior]When[Condition]` 
- `shouldThrowExceptionWhen[InvalidCondition]`

---

## ⚠️ Manejo de Excepciones

### Regla de oro: Excepciones en la capa apropiada

#### 1. Excepciones de DOMINIO (Reglas de negocio)

**Ubicación:** `domain/exceptions/` (si es necesario)
```java
// Dominio - Validación de reglas de negocio
public class Order {
    public Order(String tableNumber, List products) {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Table number cannot be null or empty");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products list cannot be null or empty");
        }
    }
}
```

**Usa:**
- `IllegalArgumentException` - Para validaciones de parámetros
- `IllegalStateException` - Para operaciones inválidas en el estado actual

#### 2. Excepciones de APLICACIÓN (Casos de uso)

**Ubicación:** `application/exceptions/`
```java
// Casos de uso - Errores de orquestación
public class ProcessOrderUseCase {
    public List execute(Order order) {
        try {
            List tasks = taskDecomposer.decompose(order);
            taskRepository.saveAll(tasks);
            return tasks;
        } catch (IllegalArgumentException e) {
            // Re-lanzar con contexto de caso de uso
            throw new OrderProcessingException("Failed to process order: " + e.getMessage(), e);
        }
    }
}
```

#### 3. Manejo de Excepciones en INFRASTRUCTURE (REST)

**Ubicación:** `infrastructure/rest/exception/GlobalExceptionHandler.java`
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleValidationException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity handleOrderProcessingException(OrderProcessingException ex) {
        ErrorResponse error = new ErrorResponse(
            "PROCESSING_ERROR",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    // Nunca exponer stack traces al cliente en producción
}
```

**ErrorResponse DTO:**
```java
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {}
```

---

## 📝 Convenciones de Código

### Declaración de Variables
```java
// ✅ BIEN - Tipos explícitos en dominio
List products = new ArrayList<>();
Map> productsByStation = new HashMap<>();

// ❌ EVITAR var en código 
var products = new ArrayList<>();  // Menos claro

```

### Métodos Cortos
```java
// ✅ BIEN - Método corto con responsabilidad única
private Map> groupProductsByStation(Order order) {
    Map> productsByStation = new HashMap<>();
    
    for (Product product : order.getProducts()) {
        Station station = product.getType().getStation();
        productsByStation
            .computeIfAbsent(station, k -> new ArrayList<>())
            .add(product);
    }
    
    return productsByStation;
}

// ❌ MAL - Método muy largo (> 30 líneas)
public List processEverything(Order order) {
    // validar...
    // mapear...
    // agrupar...
    // crear tareas...
    // ejecutar comandos...
    // persistir...
    // TODO: DIVIDIR EN MÉTODOS PEQUEÑOS
}
```

### Inmutabilidad Preferida
```java
// ✅ BIEN - Entidades de dominio inmutables
public class Product {
    private final String name;  // final siempre que sea posible
    private final ProductType type;
    
    public Product(String name, ProductType type) {
        this.name = name;
        this.type = type;
    }
    
    // Solo getters, NO setters
}

// ✅ BIEN - Copias defensivas
public class Order {
    private final List products;
    
    public List getProducts() {
        return new ArrayList<>(products);  // Copia defensiva
    }
}
```

### Nombres Descriptivos
```java
// ✅ BIEN
TaskDecomposer taskDecomposer;
Map> productsByStation;

// ❌ MAL
TaskDecomposer td;
Map> map;
```

---

## 🎯 Patrones de Diseño en el Proyecto

### 1. Command Pattern (Core del ejercicio)
```java
public interface Command {
    void execute();
}

public class StartTaskCommand implements Command {
    private final Task task;
    
    @Override
    public void execute() {
        task.start();
    }
}
```

### 2. Repository Pattern
```java
// Port (interface en application)
public interface TaskRepository {
    void saveAll(List tasks);
    Optional findById(Long id);
}

// Adapter (implementación en infrastructure)
@Component
public class TaskRepositoryAdapter implements TaskRepository {
    private final TaskJpaRepository jpaRepository;
    private final TaskEntityMapper mapper;
    // ...
}
```

### 3. Factory Pattern
```java
public class TaskFactory {
    public List createTasks(String tableNumber, Map> productsByStation) {
        List tasks = new ArrayList<>();
        
        for (Map.Entry> entry : productsByStation.entrySet()) {
            tasks.add(new Task(entry.getKey(), tableNumber, entry.getValue()));
        }
        
        return tasks;
    }
}
```

---

## 🚫 Anti-Patrones a Evitar

### 1. God Objects
```java
// ❌ MAL - Clase que hace demasiado
class OrderService {
    void validate() {}
    void map() {}
    void persist() {}
    void notify() {}
    void log() {}
    // DIVIDIR EN CLASES COHESIVAS
}
```

### 2. Primitive Obsession
```java
// ❌ MAL
public void processOrder(String tableNumber, List<Map> products) {}

// ✅ BIEN
public void processOrder(Order order) {}
```

### 3. Anemic Domain Model
```java
// ❌ MAL - Solo getters/setters, sin lógica
class Task {
    private TaskStatus status;
    public void setStatus(TaskStatus status) { this.status = status; }
}

// ✅ BIEN - Con comportamiento de negocio
class Task {
    private TaskStatus status;
    
    public void start() {
        if (status != TaskStatus.PENDIENTE) {
            throw new IllegalStateException("Cannot start non-pending task");
        }
        this.status = TaskStatus.EN_PREPARACION;
        this.startedAt = LocalDateTime.now();
    }
}
```

---

## 📚 Checklist Antes de Commit

Antes de generar código, verifica:

- [ ] ✅ El código cumple SRP (una responsabilidad por clase/método)
- [ ] ✅ Hay test ANTES del código (TDD)
- [ ] ✅ No hay dependencias invertidas (domain no importa de infra)
- [ ] ✅ Métodos < 20 líneas
- [ ] ✅ Nombres descriptivos (no abreviaciones)
- [ ] ✅ Excepciones manejadas en la capa apropiada
- [ ] ✅ Copias defensivas en getters de colecciones
- [ ] ✅ No hay código comentado o TODOs sin resolver
- [ ] ✅ Variables tipadas explícitamente (evitar var en dominio)
- [ ] ✅ Cumple con alguna User Story de `HISTORIAS_DE_USUARIO.md`

---

## 🎓 Filosofía del Proyecto

> "Este código será evaluado por su **calidad arquitectónica**, no solo por funcionar. Cada línea debe demostrar comprensión de principios de ingeniería de software."

**Prioridades (en orden):**
1. Arquitectura correcta (Hexagonal)
2. Principios SOLID
3. Tests de calidad (TDD)
4. Clean Code
5. Funcionalidad completa

---

## 📖 Comandos de Referencia
```bash
# Ejecutar tests (SIEMPRE antes de commit)
./gradlew test

# Build completo
./gradlew clean build

# Ejecutar aplicación
./gradlew bootRun
```

---