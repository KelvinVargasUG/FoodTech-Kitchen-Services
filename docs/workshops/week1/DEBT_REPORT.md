# Reporte Ejecutivo de Deuda Técnica

**Fecha:** 2026-02-20
**Alcance:** Violaciones SOLID y deriva arquitectónica (intención hexagonal vs. implementación real) en el backend Spring Boot.
**Repositorio:** FoodTech-Kitchen-Services

---

## Principales Violaciones SOLID

### 1) Violación de SRP — Caso de uso con múltiples responsabilidades (orquestación + ejecución asíncrona + persistencia + logging)

**Principio:** Single Responsibility Principle
**Anti-patrón:** God Method / Blob de orquestación

**Evidencia**
- `src/main/java/com/foodtech/kitchen/application/usecases/StartTaskPreparationUseCase.java` — líneas 42-84

**Extracto mínimo**
```java
// StartTaskPreparationUseCase.java, líneas 42-76
@Override
@Transactional
public Task execute(Long taskId) {
    Task task = taskRepository.findById(taskId)                          // 1. Lectura de persistencia
            .orElseThrow(() -> new TaskNotFoundException(taskId));

    task.start();                                                        // 2. Transición de estado de dominio
    Task savedTask = taskRepository.save(task);                          // 3. Escritura de persistencia
    updateOrderInProgress(savedTask.getOrderId());                       // 4. Orquestación de Order

    Mono.fromRunnable(() -> {                                            // 5. Infraestructura reactiva
                Command command = commandFactory.createCommand(
                        savedTask.getStation(), savedTask.getProducts());
                commandExecutor.execute(command);
            })
            .subscribeOn(Schedulers.boundedElastic())                    // 6. Scheduler de infraestructura
            .doOnSuccess(unused -> {
                Task completedTask = taskRepository.findById(taskId)     // 7. Segunda lectura de persistencia
                        .orElseThrow(() -> new TaskNotFoundException(taskId));
                completedTask.complete();                                // 8. Segunda transición de dominio
                taskRepository.save(completedTask);                      // 9. Segunda escritura
                orderCompletionService.completeOrderIfReady(             // 10. Delegación de completitud
                        completedTask.getOrderId());
                System.out.println("✅ [REACTOR] Task " + taskId        // 11. Logging a stdout
                        + " completed");
            })
            .doOnError(error -> {
                System.err.println("❌ [REACTOR] Error in task "         // 12. Logging a stderr
                        + taskId);
                error.printStackTrace();
            })
            .subscribe();                                                // 13. Fire-and-forget

    return savedTask;
}
```

**¿Por qué funciona hoy?**
Spring Boot gestiona transacciones automáticamente mediante `@Transactional`, y Reactor proporciona ejecución asíncrona sin requerir infraestructura adicional. Con pocas estaciones y bajo volumen, el fire-and-forget no causa problemas visibles.

**¿Por qué no es mantenible?**
- **13 responsabilidades concretas** en un solo método de 34 líneas: persistencia (lectura/escritura ×2), transiciones de dominio (×2), orquestación de orden, creación de comando, ejecución asíncrona, scheduling, logging (×2) y delegación de completitud.
- El `Mono.subscribe()` lanza ejecución fuera del scope transaccional de Spring — los callbacks `doOnSuccess`/`doOnError` se ejecutan sin `@Transactional`, lo cual es una bomba de tiempo para inconsistencia de datos.
- Probar el caso de uso requiere mockear Reactor (`StepVerifier`) además de repositorios, aumentando la complejidad de las pruebas unitarias.

**Escenario de impacto**
Agregar políticas de reintento, timeouts, dead-letter queue o un audit trail para la ejecución de comandos obliga a modificar este caso de uso, retestando simultáneamente reglas de dominio, persistencia y concurrencia. Un bug en el logging podría romper la cadena reactiva completa.

**Recomendación de refactor (alineada a hexagonal, sin cambio de comportamiento)**
1. Extraer la ejecución asíncrona a un puerto de salida: `AsyncCommandDispatcher` (interfaz en `application/ports/out/`), implementado por un adaptador Reactor en `infrastructure/execution/`.
2. Mover los callbacks de completitud de tarea al adaptador, invocando un puerto de entrada o servicio de aplicación para la transición post-ejecución.
3. Reemplazar `System.out.println`/`System.err.println` por un puerto de logging o, como mínimo, por SLF4J inyectado.
4. Mantener el caso de uso limitado a: iniciar tarea → persistir → despachar comando asincrónicamente.

**Métricas**
| Métrica | Valor |
|---|---|
| Superficie de cambio | 5-7 archivos/clases (use case, nuevo puerto, adaptador, callback handler, config, pruebas unitarias, prueba de integración) |
| Hotspots de acoplamiento | Reactor `Mono`/`Schedulers`, `TaskRepository`, `OrderRepository`, `CommandFactory`, `CommandExecutor`, `OrderCompletionService` (6 dependencias directas) |
| Test Friction Score | **Alto** — efectos secundarios asíncronos con fire-and-forget impiden verificación determinista; se requiere `StepVerifier` o latches para pruebas |
| Riesgo | **Medio-Alto** |
| Esfuerzo | **M** |

---

### 2) Violación de DIP — La capa de aplicación depende directamente del framework (Spring + Reactor + Jackson)

**Principio:** Dependency Inversion Principle
**Anti-patrón:** Framework Coupling en capas internas

**Evidencia**
Las siguientes clases dentro del paquete `application/usecases/` importan directamente anotaciones y clases de Spring, Reactor o Jackson:

| Clase | Línea(s) | Importaciones de framework |
|---|---|---|
| `StartTaskPreparationUseCase.java` | 13-16, 19, 43 | `@Service`, `@Transactional`, `reactor.core.publisher.Mono`, `reactor.core.scheduler.Schedulers` |
| `ProcessOrderUseCase.java` | 9-10, 14, 32 | `@Service`, `@Transactional` |
| `GetCompletedOrdersUseCase.java` | 10, 19 | `@Service` |
| `OrderCompletionService.java` | 9-10, 12, 23 | `@Service`, `@Transactional` |
| `RequestOrderInvoiceUseCase.java` | 10, 27 | `@Transactional` |
| `InvoicePayloadBuilder.java` | 3-4, 7, 13 | `@Component`, `ObjectMapper`, `JsonProcessingException` |

**Extracto mínimo**
```java
// ProcessOrderUseCase.java, líneas 9-14
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service                                         // ← framework en application layer
public class ProcessOrderUseCase implements ProcessOrderPort {
```

```java
// InvoicePayloadBuilder.java, líneas 3-4, 7, 13
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component                                       // ← framework en application layer
public class InvoicePayloadBuilder {
```

**¿Por qué funciona hoy?**
Spring escanea automáticamente `@Service`/`@Component` y crea los proxies transaccionales. El `ObjectMapper` de Jackson está disponible como bean global. El wiring es implícito y conveniente.

**¿Por qué no es mantenible?**
- **Inversión de dependencia violada:** los módulos de alto nivel (casos de uso) dependen de módulos de bajo nivel (Spring Framework, Reactor, Jackson). En hexagonal, la capa de aplicación no debería tener dependencias hacia frameworks — el wiring debe vivir en infraestructura/configuración.
- **Inconsistencia interna del propio proyecto:** `ApplicationConfig.java` ya define beans explícitos para `ProcessOrderUseCase`, `StartTaskPreparationUseCase`, `GetOrderStatusUseCase` y `RequestOrderInvoiceUseCase` (líneas 57-112). Sin embargo, esas mismas clases también llevan `@Service`, lo que genera definiciones duplicadas que Spring resuelve por convención pero que violan la legibilidad y la intención arquitectónica.
- **`InvoicePayloadBuilder`** acopla la serialización JSON (Jackson) a la capa de aplicación. Si se requiere otro formato (XML, Protobuf, Avro) o se cambia la librería de serialización, hay que modificar una clase de aplicación.

**Escenario de impacto**
Ejecutar los casos de uso en un contexto no-Spring (batch, CLI, AWS Lambda, pruebas unitarias puras sin `@SpringBootTest`) requiere levantar un contexto de Spring o reescribir las clases. Migrar de Jackson a otra librería de serialización obliga a cambiar `InvoicePayloadBuilder` en la capa de aplicación.

**Recomendación de refactor (alineada a hexagonal, sin cambio de comportamiento)**
1. Eliminar `@Service` y `@Transactional` de todas las clases en `application/usecases/`. El wiring ya existe en `ApplicationConfig.java`.
2. Gestionar transacciones en la capa de infraestructura: mover `@Transactional` a los adaptadores de persistencia o a una clase de configuración transaccional AOP.
3. Extraer `InvoicePayloadBuilder` como un puerto de salida (`InvoicePayloadSerializer`) con implementación Jackson en `infrastructure/serialization/`.
4. Eliminar imports de `reactor.*` del caso de uso (cubierto por la recomendación de la Violación #1).

**Métricas**
| Métrica | Valor |
|---|---|
| Superficie de cambio | 6-8 archivos/clases (5 use cases + InvoicePayloadBuilder + ApplicationConfig + nuevo puerto/adaptador de serialización) |
| Hotspots de acoplamiento | `@Service` (5 clases), `@Transactional` (4 clases), `@Component` (1 clase), `ObjectMapper` (1 clase), `reactor.*` (1 clase) |
| Test Friction Score | **Medio** — se requiere `@SpringBootTest` o contexto de Spring para validar wiring y transacciones, impidiendo pruebas unitarias puras |
| Riesgo | **Bajo-Medio** |
| Esfuerzo | **M** |

---

### 3) Violación de OCP — Factory con `switch` sobre enum que obliga a modificar código para cada nueva estación

**Principio:** Open/Closed Principle
**Anti-patrón:** Shotgun Surgery / Rigid Switch

**Evidencia**
- `src/main/java/com/foodtech/kitchen/domain/services/CommandFactory.java` — líneas 11-16

**Extracto mínimo**
```java
// CommandFactory.java, líneas 11-16
public Command createCommand(Station station, List<Product> products) {
    return switch (station) {
        case BAR -> new PrepareDrinkCommand(products);
        case HOT_KITCHEN -> new PrepareHotDishCommand(products);
        case COLD_KITCHEN -> new PrepareColdDishCommand(products);
    };
}
```

**Clases afectadas en cadena (shotgun surgery):**
Agregar una nueva estación (e.g., `DESSERT`) requiere modificar:

| # | Archivo | Cambio necesario |
|---|---|---|
| 1 | `domain/model/Station.java` (L3-7) | Agregar `DESSERT` al enum |
| 2 | `domain/model/ProductType.java` (L7-10) | Agregar `DESSERT_DISH(Station.DESSERT)` |
| 3 | `domain/services/CommandFactory.java` (L12-15) | Agregar `case DESSERT ->` |
| 4 | Crear `domain/commands/PrepareDessertCommand.java` | Nueva clase |
| 5 | `CommandFactoryTest.java` | Nuevos test cases |

**¿Por qué funciona hoy?**
El set de estaciones es pequeño (3) y estable. El `switch` exhaustivo de Java 17+ garantiza error de compilación si se agrega un enum sin cubrir el case. La simplicidad es suficiente para el alcance actual.

**¿Por qué no es mantenible?**
- Cada nueva estación exige editar la factory, violando OCP: la clase no está cerrada para modificación.
- El `switch` es un point of rigidity: concentra el conocimiento de todas las estaciones y sus comandos en un solo lugar. Con más estaciones, el método crece linealmente.
- Todas las pruebas del factory requieren revalidación ante cualquier nueva estación, incluso si la nueva implementación de `Command` es correcta en aislamiento.

**Escenario de impacto**
Si el negocio agrega las estaciones `DESSERT`, `GRILL` y `SUSHI_BAR` en un trimestre, esta factory se modifica 3 veces, cada una con riesgo de regresión sobre las estaciones existentes. El equipo no puede agregar una nueva estación de forma independiente (módulo cerrado).

**Recomendación de refactor (alineada a hexagonal, sin cambio de comportamiento)**
1. Definir una interfaz funcional `CommandBuilder` en dominio: `(List<Product>) -> Command`.
2. Reemplazar el `switch` por un `Map<Station, CommandBuilder>` inyectado.
3. Registrar los builders en `ApplicationConfig.java` (infraestructura/configuración):
   ```java
   @Bean
   public CommandFactory commandFactory() {
       Map<Station, CommandBuilder> builders = Map.of(
           Station.BAR, PrepareDrinkCommand::new,
           Station.HOT_KITCHEN, PrepareHotDishCommand::new,
           Station.COLD_KITCHEN, PrepareColdDishCommand::new
       );
       return new CommandFactory(builders);
   }
   ```
4. Agregar una nueva estación se reduce a: crear la clase `Command` + registrar en configuración. **Cero cambios** en `CommandFactory`.

**Métricas**
| Métrica | Valor |
|---|---|
| Superficie de cambio | 3-4 archivos/clases (factory, nueva interfaz `CommandBuilder`, `ApplicationConfig`, pruebas) |
| Hotspots de acoplamiento | Enum `Station`, clases concretas `Prepare*Command` (3 clases), `CommandFactory` |
| Test Friction Score | **Medio** — cada rama del `switch` requiere su propio test; agregar estación obliga revalidación completa |
| Riesgo | **Bajo** |
| Esfuerzo | **S-M** |

---

## Intención vs. Realidad (Deriva Hexagonal)

### Resumen de capas esperadas vs. observadas

```
ESPERADO (Hexagonal)                    OBSERVADO (Real)
┌─────────────────────┐                 ┌─────────────────────┐
│   Infrastructure     │                │   Infrastructure     │
│  (Spring, JPA, Web)  │                │  (Spring, JPA, Web)  │
│         │            │                │       │    ▲         │
│         ▼            │                │       ▼    │         │
│    Application       │                │    Application       │
│  (puertos, use cases)│                │  @Service @Transact. │
│    sin framework     │                │  Reactor, Jackson    │
│         │            │                │       │    ▲         │
│         ▼            │                │       ▼    │         │
│      Domain          │                │      Domain          │
│  (modelos, servicios)│                │  (modelos, servicios)│
│    puro Java         │                │    puro Java ✅      │
└─────────────────────┘                 └─────────────────────┘
```

### Ejemplo concreto 1: Casos de uso anotados como beans de framework

**Archivo(s):**
- `src/main/java/com/foodtech/kitchen/application/usecases/ProcessOrderUseCase.java` — líneas 9, 14 (`@Service`)
- `src/main/java/com/foodtech/kitchen/application/usecases/StartTaskPreparationUseCase.java` — líneas 13, 15-16, 19 (`@Service`, Reactor imports)
- `src/main/java/com/foodtech/kitchen/application/usecases/OrderCompletionService.java` — líneas 9, 12 (`@Service`)
- `src/main/java/com/foodtech/kitchen/application/usecases/GetCompletedOrdersUseCase.java` — líneas 10, 19 (`@Service`)

**Qué debería ocurrir:** Los casos de uso son POJOs puros instanciados por configuración de infraestructura.
**Qué ocurre realmente:** Llevan `@Service` y `@Transactional`, lo que acopla la capa de aplicación al ciclo de vida de Spring. Existe un conflicto adicional: `ApplicationConfig.java` (líneas 57-112) ya declara beans para estos mismos use cases, generando definiciones redundantes.

### Ejemplo concreto 2: Controller que elude puertos de aplicación y accede directamente a repositorio

**Archivo:** `src/main/java/com/foodtech/kitchen/infrastructure/rest/TaskController.java` — líneas 5, 22, 37-40

**Extracto:**
```java
// TaskController.java, líneas 5, 22
import com.foodtech.kitchen.application.ports.out.TaskRepository;  // puerto de SALIDA importado por adaptador de ENTRADA
private final TaskRepository taskRepository;                        // ← bypass de puertos de entrada

// TaskController.java, líneas 36-41
if (status != null) {
    tasks = taskRepository.findByStationAndStatus(station, status); // ← acceso directo a persistencia
} else {
    tasks = getTasksByStationPort.execute(station);                 // ← usa el puerto (correcto)
}
```

**Qué debería ocurrir:** El controller (adaptador de entrada) invoca exclusivamente puertos de entrada (`GetTasksByStationPort`). La lógica de filtrado por status debería estar en el caso de uso o en un puerto de entrada adicional.
**Qué ocurre realmente:** El controller importa un puerto de **salida** (`TaskRepository`) y ejecuta queries de persistencia directamente, mezclando la responsabilidad de adaptador web con la de acceso a datos. Esto crea un cortocircuito en la arquitectura hexagonal: el adaptador de entrada habla directamente con el adaptador de salida sin pasar por la lógica de aplicación.

---

## Notas Finales

- **Capa de dominio: limpia.** Las clases en `domain/model/` y `domain/services/` no tienen dependencias de framework. Este es el punto más fuerte del proyecto y debería preservarse.
- Todos los hallazgos se centran en mejoras **estructurales y organizacionales** sin alterar el comportamiento del sistema.
- Las recomendaciones priorizan restaurar los límites hexagonales y reducir la fricción de pruebas para habilitar un refactor incremental y medible.
