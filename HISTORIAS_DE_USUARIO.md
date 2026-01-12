# 📋 Historias de Usuario - FoodTech Kitchen Service

## 🎯 Principios INVEST

Todas las historias de usuario de este proyecto cumplen con los principios INVEST:

- **I**ndependent (Independiente): Cada historia puede desarrollarse y entregarse por separado
- **N**egotiable (Negociable): Los detalles pueden refinarse con el equipo
- **V**aluable (Valiosa): Aporta valor observable al negocio
- **E**stimable (Estimable): Se puede estimar el esfuerzo necesario
- **S**mall (Pequeña): Se puede completar en una iteración
- **T**estable (Testeable): Se puede verificar su cumplimiento

---

## HU-001: Procesar pedido de cocina

### Descripción

**Como** responsable de cocina  
**Quiero** que el sistema reciba un pedido y lo descomponga automáticamente en tareas por estación  
**Para** que cada área de preparación pueda trabajar de forma independiente y eficiente

### Contexto de Negocio

Actualmente, cuando llega un pedido al restaurante, el responsable de cocina debe leer manualmente todos los productos y asignarlos a las diferentes estaciones (barra, cocina caliente, cocina fría). Este proceso manual genera:
- Demoras en la preparación
- Errores de asignación
- Productos olvidados
- Falta de visibilidad por estación

La solución automatiza esta descomposición, permitiendo que cada estación reciba únicamente sus tareas correspondientes de manera inmediata.

### Valor de Negocio

- Reducción del tiempo de procesamiento de pedidos
- Eliminación de errores de asignación manual
- Mayor eficiencia operativa en cocina
- Mejor experiencia del cliente por tiempos de preparación optimizados

---

### Criterios de Aceptación

#### Escenario 1: Pedido con un solo tipo de producto

```gherkin
Scenario: Pedido únicamente con bebidas
  Given que existe un pedido para la mesa "A1"
  And el pedido contiene 2 bebidas diferentes
  When el pedido es registrado en el sistema
  Then el sistema genera 1 tarea de preparación
  And la tarea es asignada a la estación de barra
  And la tarea contiene los 2 productos solicitados
```

#### Escenario 2: Pedido mixto con múltiples tipos de productos

```gherkin
Scenario: Pedido con bebidas, plato caliente y postre
  Given que existe un pedido para la mesa "B5"
  And el pedido contiene 1 bebida
  And el pedido contiene 1 plato principal
  And el pedido contiene 1 postre
  When el pedido es registrado en el sistema
  Then el sistema genera 3 tareas de preparación
  And existe 1 tarea asignada a la estación de barra
  And existe 1 tarea asignada a la estación de cocina caliente
  And existe 1 tarea asignada a la estación de cocina fría
  And cada tarea contiene únicamente los productos de su estación correspondiente
```

#### Escenario 3: Agrupación de productos similares

```gherkin
Scenario: Múltiples productos del mismo tipo se agrupan en una sola tarea
  Given que existe un pedido para la mesa "C2"
  And el pedido contiene 3 bebidas diferentes
  And el pedido contiene 2 platos principales diferentes
  When el pedido es registrado en el sistema
  Then el sistema genera 2 tareas de preparación
  And la tarea de barra contiene las 3 bebidas agrupadas
  And la tarea de cocina caliente contiene los 2 platos agrupados
```

#### Escenario 4: Pedido sin productos no puede ser procesado

```gherkin
Scenario: Sistema rechaza pedidos vacíos
  Given que existe un pedido para la mesa "D3"
  And el pedido no contiene ningún producto
  When se intenta registrar el pedido en el sistema
  Then el sistema rechaza el pedido
  And se notifica que el pedido debe contener al menos un producto
  And no se genera ninguna tarea de preparación
```

#### Escenario 5: Validación de información mínima requerida

```gherkin
Scenario: Pedido sin identificación de mesa no puede ser procesado
  Given que existe un pedido sin número de mesa asignado
  And el pedido contiene 2 productos válidos
  When se intenta registrar el pedido en el sistema
  Then el sistema rechaza el pedido
  And se notifica que el pedido debe tener un número de mesa válido
  And no se genera ninguna tarea de preparación
```

---

## HU-002: Consultar tareas por estación

### Descripción

**Como** encargado de una estación de cocina  
**Quiero** visualizar únicamente las tareas pendientes de mi estación  
**Para** prepararlas sin confusión con tareas de otras áreas

### Contexto de Negocio

Cada estación de cocina (barra, cocina caliente, cocina fría) necesita ver solamente sus propias tareas pendientes. Si todas las estaciones ven todas las tareas, se genera:
- Confusión sobre qué preparar
- Duplicación de esfuerzos
- Tareas olvidadas o no realizadas
- Pérdida de tiempo identificando responsabilidades

La solución permite que cada estación consulte únicamente sus tareas asignadas.

### Valor de Negocio

- Claridad operativa por estación
- Reducción de errores de preparación
- Mejor organización del trabajo
- Mayor velocidad de ejecución

---

### Criterios de Aceptación

#### Escenario 1: Consulta de tareas de una estación específica

```gherkin
Scenario: Estación de barra consulta sus tareas pendientes
  Given que existen 3 tareas pendientes en el sistema
  And 2 tareas están asignadas a la estación de barra
  And 1 tarea está asignada a la estación de cocina caliente
  When el encargado de barra consulta las tareas de su estación
  Then el sistema muestra únicamente las 2 tareas de barra
  And no se muestran tareas de otras estaciones
```

#### Escenario 2: Estación sin tareas pendientes

```gherkin
Scenario: Estación consulta tareas cuando no tiene pendientes
  Given que existen 2 tareas pendientes en el sistema
  And ambas tareas están asignadas a la estación de cocina caliente
  And no hay tareas asignadas a la estación de barra
  When el encargado de barra consulta las tareas de su estación
  Then el sistema muestra que no hay tareas pendientes
  And se confirma que la consulta fue exitosa
```

#### Escenario 3: Información completa de cada tarea

```gherkin
Scenario: Cada tarea muestra la información necesaria para su preparación
  Given que existe 1 tarea pendiente para la estación de barra
  And la tarea corresponde al pedido de la mesa "A1"
  And la tarea contiene 2 bebidas específicas
  When el encargado de barra consulta las tareas de su estación
  Then el sistema muestra el número de mesa asociado
  And el sistema muestra la lista detallada de productos a preparar
  And el sistema muestra el momento en que se creó la tarea
```

#### Escenario 4: Validación de estación existente

```gherkin
Scenario: Consulta de estación inexistente
  Given que el sistema solo reconoce las estaciones: barra, cocina caliente y cocina fría
  When se consultan tareas para una estación no reconocida
  Then el sistema informa que la estación no existe
  And no se muestran tareas
```

---

## HU-003: Ejecutar tarea de preparación

### Descripción

**Como** cocinero de una estación  
**Quiero** iniciar la preparación de una tarea asignada  
**Para** que el sistema registre automáticamente el progreso y notifique cuando esté completada

### Contexto de Negocio

Cuando un cocinero comienza a preparar una tarea, el sistema debe:
- Registrar el inicio de la preparación
- Monitorear el progreso de la tarea
- Notificar automáticamente cuando la preparación finaliza
- Mantener visibilidad del estado en tiempo real para coordinación con meseros

Esto permite a los responsables de cocina y al área de servicio conocer el estado real de cada pedido sin intervención manual adicional.

### Valor de Negocio

- Visibilidad del estado de pedidos en tiempo real
- Reducción de errores humanos en el registro de avances
- Mejor coordinación entre estaciones de cocina y área de servicio
- Métricas automáticas de tiempos de preparación
- Liberación del cocinero para enfocarse en la preparación, no en actualizar sistemas

---

### Criterios de Aceptación

#### Escenario 1: Iniciar preparación de una tarea
```gherkin
Scenario: Cocinero inicia preparación de una tarea pendiente
  Given que existe una tarea pendiente para la estación de barra
  And la tarea está en estado "PENDIENTE"
  When el cocinero indica que inicia la preparación de la tarea
  Then el sistema cambia el estado de la tarea a "EN_PREPARACION"
  And el sistema registra la hora de inicio de preparación
```

#### Escenario 2: Sistema completa tarea automáticamente
```gherkin
Scenario: Tarea se completa automáticamente al finalizar preparación
  Given que existe una tarea en estado "EN_PREPARACION"
  And el cocinero está ejecutando la preparación física de los productos
  When el tiempo estimado de preparación transcurre
  Then el sistema cambia el estado de la tarea a "COMPLETADA" automáticamente
  And el sistema registra la hora de finalización
  And el sistema calcula el tiempo total de preparación
```

#### Escenario 3: Visualización de tareas completadas por estación
```gherkin
Scenario: Consulta de tareas completadas de una estación
  Given que la estación de barra tiene 2 tareas completadas
  And la estación de barra tiene 1 tarea en preparación
  And la estación de barra tiene 1 tarea pendiente
  When el responsable consulta el historial de tareas completadas de barra
  Then el sistema muestra únicamente las 2 tareas completadas
  And cada tarea muestra su tiempo total de preparación
```

#### Escenario 4: Estado del pedido basado en estado de sus tareas
```gherkin
Scenario: Pedido refleja el estado agregado de todas sus tareas
  Given que un pedido generó 3 tareas para diferentes estaciones
  And 2 tareas ya están completadas
  And 1 tarea está en preparación
  When el área de servicio consulta el estado del pedido
  Then el sistema indica que el pedido está "EN_PREPARACION"
  
  When la última tarea se completa automáticamente
  And el área de servicio consulta nuevamente el estado del pedido
  Then el sistema indica que el pedido está "COMPLETADO"
```

#### Escenario 5: No se puede iniciar una tarea ya iniciada
```gherkin
Scenario: Validación de estado antes de iniciar preparación
  Given que existe una tarea en estado "EN_PREPARACION"
  When el cocinero intenta iniciar nuevamente la preparación de la misma tarea
  Then el sistema rechaza la operación
  And el sistema informa que la tarea ya está en preparación
  And la tarea permanece en estado "EN_PREPARACION"
```

---

## 📊 Matriz de Trazabilidad

| Historia | Estación Involucrada | Prioridad | Complejidad | Dependencies |
|----------|---------------------|-----------|-------------|--------------|
| HU-001   | Todas               | Alta      | Media       | Ninguna      |
| HU-002   | Todas               | Alta      | Baja        | HU-001       |
| HU-003   | Todas               | Media     | Media       | HU-001, HU-002 |

---

## 🎯 Orden de Implementación Sugerido

1. **Sprint 1:** HU-001 (Core del negocio - Procesamiento de pedidos)
2. **Sprint 2:** HU-002 (Consulta de tareas)
3. **Sprint 3:** HU-003 (Ejecución y seguimiento)

---

## 📝 Notas Importantes

### Lenguaje de Negocio

Todos los criterios de aceptación están escritos en **lenguaje de negocio**, no técnico:
- ✅ "el sistema genera una tarea" (no "se crea un registro en BD")
- ✅ "el pedido es registrado" (no "se hace POST al endpoint")
- ✅ "se muestra el número de mesa" (no "se retorna en el JSON response")

### Validez Tecnológica

Los criterios son **independientes de la implementación**:
- ✅ Válidos si usas REST API o GraphQL
- ✅ Válidos si usas PostgreSQL o MongoDB
- ✅ Válidos si usas Java o Python
- ✅ Válidos si cambias el frontend

### Orientación a QA

Los escenarios están escritos para que **QA pueda entenderlos y probarlos** sin conocimiento técnico del código.

---

**Versión:** 1.0  
**Fecha:** Enero 2026  
**Autor:** Carlos
