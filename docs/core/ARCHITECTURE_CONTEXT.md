# ARCHITECTURE_CONTEXT (Normativo + Estado Actual)

Cláusula de oficialidad: Este documento es la referencia arquitectónica oficial de este repositorio. Cualquier cambio estructural (capas, dependencias, ubicación de puertos, patrones, adaptación de infraestructura, seguridad, persistencia o mensajería) debe alinearse con las reglas aquí definidas o, en caso de requerir una excepción, documentar explícitamente dicha excepción en este mismo documento.

Fecha: 2026-02-24  
Repositorio/Servicio: FoodTech Kitchen Services

## 1) Propósito y alcance

Este documento combina:
- Contexto factual del estado real del sistema (stack, paquetes, puertos, dependencias observadas).
- Normativa arquitectónica obligatoria para cambios futuros.
- Decisiones arquitectónicas activas y sus implicaciones.

No describe componentes no existentes; cuando se mencionan futuros cambios (p. ej. JWT) se hace como lineamiento.

---

## 2) Stack técnico (observado)

- Java 17 + Gradle
- Spring Boot 3.2.1
- API: Spring Web (MVC)
- Persistencia: Spring Data JPA + PostgreSQL
- Seguridad: Spring Security (config presente, enfoque “modo desarrollo”)
- Asincronía: Reactor Core (uso en infraestructura)
- Serialización: Jackson (ObjectMapper configurado)
- Tests/Calidad: JUnit (starter-test), JaCoCo (umbral mínimo 0.70), reactor-test, security-test
- Lombok: habilitado

Fuente: build.gradle

---

## 3) Arquitectura adoptada

Arquitectura Hexagonal/Clean materializada en 3 capas de paquetes bajo com.foodtech.kitchen:

- Domain: modelos y reglas de negocio, sin dependencias de frameworks en lo revisado.
- Application: orquestación (casos de uso), puertos (in/out), excepciones de aplicación, outbox.
- Infrastructure: adaptadores (REST, JPA, ejecución async con Reactor, serialización Jackson), configuración Spring y wrappers transaccionales.

Estructura base:
- src/main/java/com/foodtech/kitchen/domain
- src/main/java/com/foodtech/kitchen/application
- src/main/java/com/foodtech/kitchen/infrastructure

---

## 4) Estructura de paquetes real (estado actual)

### 4.1 Domain (real)
- Modelos: src/main/java/com/foodtech/kitchen/domain/model
- Servicios de dominio: src/main/java/com/foodtech/kitchen/domain/services
- Commands: src/main/java/com/foodtech/kitchen/domain/commands
- Puerto async (ubicación actual): src/main/java/com/foodtech/kitchen/domain/ports/out/AsyncCommandDispatcher.java

### 4.2 Application (real)
- Puertos de entrada: src/main/java/com/foodtech/kitchen/application/ports/in
- Puertos de salida: src/main/java/com/foodtech/kitchen/application/ports/out
- Casos de uso: src/main/java/com/foodtech/kitchen/application/usecases
- Outbox (persistencia de evento): src/main/java/com/foodtech/kitchen/application/outbox
- Excepciones de aplicación: src/main/java/com/foodtech/kitchen/application/exepcions  
  Nota factual: el paquete está escrito como “exepcions”.

### 4.3 Infrastructure (real)
- Config: src/main/java/com/foodtech/kitchen/infrastructure/config
- REST: src/main/java/com/foodtech/kitchen/infrastructure/rest
- Persistencia:
  - Adapters: src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters
  - JPA repos: src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa
  - Entidades: src/main/java/com/foodtech/kitchen/infrastructure/persistence/jpa/entities
  - Mappers: src/main/java/com/foodtech/kitchen/infrastructure/persistence/mappers
- Ejecución: src/main/java/com/foodtech/kitchen/infrastructure/execution
- Serialización: src/main/java/com/foodtech/kitchen/infrastructure/serialization
- Wrappers transaccionales: src/main/java/com/foodtech/kitchen/infrastructure/transactional

---

## 5) Boundaries internos actuales (factual)

- Controllers REST dependen de puertos de entrada, no de repositorios:
  - src/main/java/com/foodtech/kitchen/infrastructure/rest/TaskController.java
  - src/main/java/com/foodtech/kitchen/infrastructure/rest/OrderController.java
- Casos de uso en application son POJOs sin anotaciones Spring (wiring explícito):
  - src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java
- Persistencia está detrás de puertos application/ports/out con adaptadores JPA en infraestructura.
- Serialización JSON está detrás del puerto PayloadSerializer (implementación Jackson en infraestructura).

---

## 6) Estado actual del sistema (resumen operacional)

- Gestión de órdenes y tareas vía API REST.
- Descomposición de orden en tareas por estación (dominio + aplicación).
- Inicio de preparación de tareas y despacho asíncrono de ejecución de comandos (Reactor en infraestructura).
- Solicitud de factura: genera y persiste un evento Outbox, marcando la orden como “INVOICED” si aplica.

---

## 7) Riesgos / deuda estructural detectada (factual)

- Duplicación potencial de bean: ReactorAsyncCommandDispatcher tiene @Component y además se instancia por @Bean en ApplicationConfig. Esto puede causar ambigüedad o comportamiento no intencional.
- Async fire-and-forget + logging a stdout/stderr en dispatcher Reactor: observabilidad y manejo de errores básicos (sin garantías de entrega ni política explícita de retries).
- Outbox parcial: se persiste el evento, pero no se observa componente de publicación/worker/scheduler (@Scheduled, @EventListener, etc.).
- Seguridad en modo desarrollo: configuración permisiva (permitAll y CSRF deshabilitado) en SecurityConfig.

---

# 8) Reglas Arquitectónicas Obligatorias

Estas reglas aplican a todo cambio nuevo (features, refactors, integraciones).

## 8.1 Reglas de dependencia (qué puede depender de qué)

- Domain
  - Puede depender de: Java estándar y paquetes del propio domain.
  - Prohibido depender de: Spring, Reactor, Jackson, JPA/Hibernate, infraestructura, controladores, DTOs REST.
- Application
  - Puede depender de: Domain + application/ports/* + Java estándar.
  - Prohibido depender de: Spring, Reactor, Jackson, JPA/Hibernate, paquetes infrastructure/*.
- Infrastructure
  - Puede depender de: Domain + Application + frameworks (Spring/JPA/Reactor/Jackson).
  - Responsabilidad: implementar adaptadores, wiring, concerns transversales (transacciones, seguridad, serialización, ejecución async).

Regla práctica: “Los frameworks mueren en infraestructura”.

## 8.2 Dónde deben vivir los puertos

- Puertos de entrada (Input Ports): siempre en src/main/java/com/foodtech/kitchen/application/ports/in.
- Puertos de salida (Output Ports): por defecto en src/main/java/com/foodtech/kitchen/application/ports/out.
- Excepción controlada: un puerto puede vivir en domain solo si el dominio lo requiere de forma directa (no solo como conveniencia de un caso de uso). Si no, debe vivir en application.

## 8.3 Prohibiciones explícitas

- No usar anotaciones Spring (@Service, @Component, @Transactional, etc.) en Domain ni Application.
- No usar Reactor (Mono, Schedulers, subscribe, etc.) en Domain ni Application.
- No usar Jackson (ObjectMapper, JsonProcessingException, etc.) en Domain ni Application.
- No usar JPA (entidades, repositorios Spring Data) fuera de Infrastructure.
- No permitir que controllers REST dependan de puertos de salida (repositorios) ni que contengan lógica de negocio.

## 8.4 Cómo implementar nuevos casos de uso

- Crear clase en src/main/java/com/foodtech/kitchen/application/usecases como POJO:
  - Dependencias solo por constructor.
  - Depender únicamente de:
    - Puertos de salida (interfaces)
    - Servicios/modelos de dominio
- Exponer el caso de uso mediante un puerto en application/ports/in.
- Registrar el wiring en src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java.
- Si requiere transacciones, aplicar la misma política actual: wrapper transaccional en src/main/java/com/foodtech/kitchen/infrastructure/transactional (no anotaciones en application).

## 8.5 Cómo crear nuevos adapters

- Entrada (HTTP): crear controller en src/main/java/com/foodtech/kitchen/infrastructure/rest que dependa de puertos de entrada.
- Salida (DB/JPA): crear adapter en src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters que implemente un puerto application/ports/out.
- Salida (integraciones/async/serialización): crear adapter en infrastructure/* implementando un puerto; el puerto debe estar en application salvo excepción justificada.
- Regla de wiring: evitar doble registro. Si un bean se declara por @Bean, no debe ser además @Component (y viceversa).

## 8.6 Regla de excepciones (obligatoria)

Cualquier excepción a estas reglas (dependencias, ubicación de puertos, uso de frameworks, wiring, etc.) debe documentarse explícitamente en la sección “Decisiones Arquitectónicas Activas”, incluyendo:
- Qué regla se exceptúa
- Por qué se exceptúa
- Alcance/impacto
- Alternativas consideradas (si aplica)
- Plan de reversión o criterio de salida (si aplica)

---

# 9) Decisiones Arquitectónicas Activas

## 9.1 AsyncCommandDispatcher ubicado en domain (decisión y debate)

Hecho actual:
- Interfaz: src/main/java/com/foodtech/kitchen/domain/ports/out/AsyncCommandDispatcher.java
- Uso directo desde application: src/main/java/com/foodtech/kitchen/application/usecases/StartTaskPreparationUseCase.java
- Implementación Reactor en infraestructura: src/main/java/com/foodtech/kitchen/infrastructure/execution/ReactorAsyncCommandDispatcher.java

Lectura arquitectónica:
- La justificación (evitar dependencias de concurrencia en capas internas) es válida.
- El debate es de ubicación: por orquestación, este puerto encaja más naturalmente como puerto de salida de application.

Decisión activa: se mantiene como está por ahora, pero cualquier expansión (reintentos, DLQ, etc.) debe reforzar que los frameworks sigan aislados en infraestructura.

## 9.2 Outbox implementado parcialmente (persistencia sin publicación)

Hecho actual:
- Se crea y guarda un evento Outbox desde application: src/main/java/com/foodtech/kitchen/application/usecases/RequestOrderInvoiceUseCase.java
- Existe repositorio y adapter JPA para persistir.
- No se observa publisher/worker/scheduler.

Decisión activa:
- Se acepta como “Outbox persistente” (fase 1).

Riesgo activo: sin publicación, el Outbox no garantiza integración externa; debe tratarse como feature incompleto si se requiere entrega real.

## 9.3 Seguridad en modo desarrollo

Hecho actual:
- Configuración permisiva y CSRF deshabilitado en src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java
- No se observan filtros custom ni autenticación real.

Decisión activa:
- Se considera explícitamente “modo desarrollo”.

Implicación: antes de exponer a entornos reales, se debe endurecer.

---

# 10) Implicaciones para futuros features (ej. JWT)

## 10.1 Ubicación y límites (no romper pureza)

- JWT y autenticación deben vivir en Infrastructure, típicamente bajo infrastructure/config y/o un subpaquete dedicado dentro de infraestructura (p. ej. infrastructure/security o infrastructure/auth) como convención de organización.
- Application y Domain deben permanecer libres de Spring Security y clases JWT.

Si application necesita “saber” si un usuario puede realizar una acción, debe expresarse vía:
- Datos/claims ya resueltos por infraestructura y pasados como parámetros (si aplica), o
- Puertos/abstracciones en application (sin tipos Spring).

## 10.2 Endurecimiento de SecurityConfig

- permitAll() debe reemplazarse por reglas explícitas por endpoint/rol.
- CSRF debe revisarse según el tipo de cliente (SPA, server-to-server, etc.).
- Si se agregan filtros (JWT), deben implementarse como componentes de infraestructura y conectarse al SecurityFilterChain, sin filtrarse a application/domain.

## 10.3 “JWT como bounded context o subdominio” (lineamiento)

Estado actual factual: no hay paquetes separados tipo orders/ o tasks/; el sistema está organizado por capas.

Lineamiento:
- Si JWT crece en complejidad (refresh tokens, revocación, auditoría, multi-tenant), debe organizarse como subdominio/área claramente aislada (mínimo: subpaquete dedicado en infraestructura + puertos explícitos si application requiere capacidades).
- El objetivo es evitar dispersión de seguridad por múltiples paquetes y, sobre todo, evitar dependencias de framework dentro de application/domain.

---

Fin.
