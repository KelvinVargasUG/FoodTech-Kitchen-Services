# Test Cases — Product Catalog Management (HU-01 a HU-05)

| Campo    | Detalle                          |
| -------- | -------------------------------- |
| Versión  | 1.2                             |
| Fecha    | 07/04/2026                       |
| Proyecto | FoodTech Kitchen                 |
| Área     | QA                               |
| Ref.     | TEST_PLAN_NEW_FEATURE.md v2.0    |

---

# Clasificación de Test Suites

| ID | Test Suite | Proyecto | Nivel de Prueba | Herramienta / Framework | Total TCs |
|----|------------|----------|-----------------|-------------------------|:---------:|
| TS-01 | Unit Tests — Casos de Uso | FoodTech-Kitchen-Services | Unitaria | JUnit 5 + Mockito | 37 |
| TS-02 | Unit Tests — Modelo de Dominio | FoodTech-Kitchen-Services | Unitaria | JUnit 5 | 14 |
| TS-03 | Integration Tests — Controllers REST | FoodTech-Kitchen-Services | Integración | Spring Boot Test + MockMvc + H2 | 18 |
| TS-04 | Component Tests — Frontend (Hooks, Services, Componentes) | FoodTech-Front | Componente | Vitest + React Testing Library | 26 |
| TS-05 | E2E Tests — Front Screenplay | AUTO_FRONT_SCREENPLAY | End-to-End | Selenium + Serenity BDD (Screenplay) | 13 |
| TS-06 | E2E Tests — Front POM/Factory | AUTO_FRONT_POM_FACTORY | End-to-End | Selenium + Serenity BDD (POM) | 13 |
| TS-07 | E2E Tests — API REST | AUTO_API_SCREENPLAY | End-to-End | Serenity BDD + REST Assured | 15 |

### Resumen por Test Suite

```
TS-01  Unit — Casos de Uso       ████████████████████████████████████████████  37 TCs  (27.2%)
TS-02  Unit — Dominio            █████████████████                         14 TCs  (10.8%)
TS-03  Integration — REST        ██████████████████████                    18 TCs  (13.8%)
TS-04  Component — Frontend      ███████████████████████████████           26 TCs  (19.1%)
TS-05  E2E — Front Screenplay    ████████████████                          13 TCs  (10.0%)
TS-06  E2E — Front POM           ████████████████                          13 TCs  (10.0%)
TS-07  E2E — API                 ██████████████████                        15 TCs  (11.5%)
                                                                   TOTAL: 136 TCs
```
---

# HU-01: Crear Producto

## Escenario 1 — TC-HU01-01: Creación exitosa de producto

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un producto nuevo se crea correctamente con todos sus campos y estado ACTIVE |
| **Técnica** | Partición de equivalencia — datos válidos |
| **Prioridad** | Alta |
| **Precondiciones** | El repositorio está disponible; no existe producto con el mismo nombre |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no existe un producto con nombre "Pizza" en el sistema
When el administrador registra un producto con nombre "Pizza", tipo HOT_DISH, categoría "Italian" y precio 10
Then el producto se crea exitosamente con estado Activo
And el sistema almacena el producto en la base de datos
And el producto retornado tiene nombre "Pizza" y categoría "Italian"
```

**Resultado esperado:** El producto queda registrado con estado Activo y todos sus campos son los ingresados por el administrador.


---

## Escenario 2 — TC-HU01-02: Rechazo por nombre duplicado

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el sistema rechaza crear un producto con nombre ya existente |
| **Técnica** | Prueba negativa — nombre duplicado |
| **Prioridad** | Alta |
| **Precondiciones** | Existe un producto con nombre "Pizza" en el repositorio |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que ya existe un producto con nombre "Pizza" en el sistema
When el administrador intenta crear otro producto con nombre "Pizza"
Then el sistema rechaza la creación e informa que el nombre ya está en uso
And no se registra ningún producto adicional
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que ya existe un producto con ese nombre; no se crea ningún registro nuevo.


---

## Escenario 3 — TC-HU01-03: Rechazo por precio inválido (cero)

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el sistema rechaza crear un producto con precio cero |
| **Técnica** | Análisis de valores límite — precio = 0 |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no existe un producto con nombre "Pizza"
When el administrador intenta crear un producto con nombre "Pizza", tipo HOT_DISH, categoría "Italian" y precio 0
Then el sistema rechaza la creación indicando que el precio debe ser mayor a cero
And no se registra ningún producto
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que el precio no es válido; no se crea ningún registro.


---

## Escenario 4 — TC-HU01-04: Rechazo por categoría nula

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el sistema rechaza crear un producto sin categoría |
| **Técnica** | Prueba negativa — campo obligatorio nulo |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no existe un producto con nombre "Pizza"
When el administrador intenta crear un producto con nombre "Pizza", tipo HOT_DISH, sin categoría y precio 10
Then el sistema rechaza la creación indicando que la categoría es obligatoria
And no se registra ningún producto
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que la categoría es un campo obligatorio.


---

## Escenario 5 — TC-HU01-05: Validación de nombre nulo en dominio

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el modelo de dominio rechaza un nombre nulo |
| **Técnica** | Prueba negativa — validación de dominio |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Dominio (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When se intenta crear un producto sin nombre, con tipo HOT_DISH, categoría "Italian" y precio 10
Then el sistema rechaza la creación indicando que el nombre es obligatorio
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que el nombre no puede estar vacío.


---

## Escenario 6 — TC-HU01-06: Validación de nombre en blanco en dominio

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el modelo de dominio rechaza un nombre en blanco |
| **Técnica** | Análisis de valores límite — string vacío |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When se intenta crear un producto con nombre en blanco "  ", tipo HOT_DISH, categoría "Italian" y precio 10
Then el sistema rechaza la creación indicando que el nombre es obligatorio
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que el nombre no puede estar en blanco.


---

## Escenario 7 — TC-HU01-07: Validación de precio negativo en dominio

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el dominio rechaza precios negativos |
| **Técnica** | Análisis de valores límite — precio = -5 |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When se intenta crear un producto con nombre "Pizza", tipo HOT_DISH, categoría "Italian" y precio -5
Then el sistema rechaza la creación indicando que el precio debe ser mayor a cero
```

**Resultado esperado:** El sistema rechaza la solicitud indicando que el precio no es válido.


---

## Escenario 8 — TC-HU01-10: Estado ACTIVE por defecto al crear producto

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un producto recién creado tiene estado ACTIVE por defecto |
| **Técnica** | Partición de equivalencia — comportamiento por defecto |
| **Prioridad** | Alta |
| **Precondiciones** | Ninguna |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When se crea un Product con nombre "Pizza", tipo HOT_DISH, categoría "Italian" y precio 10
Then el estado del producto es ACTIVE
And el id del producto no es nulo
```

**Resultado esperado:** El producto queda registrado con estado Activo y se le asigna un identificador único.


---

## Escenario 9 — TC-HU01-11: Crear producto retorna 201 (integración)

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar flujo E2E desde HTTP POST hasta respuesta 201 con cabecera Location |
| **Técnica** | Prueba de integración — Spring Boot completo |
| **Prioridad** | Alta |
| **Precondiciones** | Usuario autenticado vía JWT; base de datos H2 disponible |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Integración (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el usuario se autentica con token JWT válido
When envía POST /api/products con nombre único, tipo HOT_DISH, categoría "Platos" y precio 1500
Then el sistema responde con status 201
And la respuesta incluye cabecera Location
And el campo status del body es "ACTIVE"
And el campo id existe en el body
```

**Resultado esperado:** El sistema responde indicando que el producto fue creado exitosamente, con su identificador y estado Activo.


---

## Escenario 10 — TC-HU01-12: Campo obligatorio faltante retorna 400 (integración)

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que la API rechaza solicitudes con campos obligatorios faltantes |
| **Técnica** | Prueba negativa — campo name ausente |
| **Prioridad** | Alta |
| **Precondiciones** | Usuario autenticado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el usuario se autentica con token JWT válido
When envía POST /api/products sin el campo nombre
Then el sistema responde con status 400
```

**Resultado esperado:** El sistema rechaza la solicitud e informa que faltan campos obligatorios.


---

## Escenario 11 — TC-HU01-13: Nombre duplicado retorna 409 (integración)

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que crear un producto con nombre repetido retorna conflicto |
| **Técnica** | Prueba negativa — duplicado E2E |
| **Prioridad** | Alta |
| **Precondiciones** | Ya existe un producto con el mismo nombre |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | E2E |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el usuario crea un producto "ProductoDuplicado" exitosamente
When envía POST /api/products con el mismo nombre "ProductoDuplicado"
Then el sistema responde con status 409
```

**Resultado esperado:** El sistema rechaza la solicitud e informa que ya existe un producto con ese nombre.


---

## Escenario 12 — TC-HU01-14: Frontend POST al servicio de productos

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el frontend envía correctamente la solicitud de creación de producto al servidor |
| **Técnica** | Prueba de componente con mock de API client |
| **Prioridad** | Media |
| **Precondiciones** | Servicio de productos disponible |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When el frontend envía la solicitud de creación del producto "Lomo Saltado"
Then la solicitud se envía correctamente al servidor
And el resultado contiene el identificador del producto y estado Activo
```

**Resultado esperado:** El producto se envía correctamente al servidor y se recibe la confirmación con identificador y estado Activo.


---

## Escenario 13 — TC-HU01-17: E2E Front Screenplay — Creación exitosa

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar E2E la creación de un producto desde la UI con Selenium |
| **Técnica** | Prueba E2E — patrón Screenplay |
| **Prioridad** | Alta |
| **Precondiciones** | El administrador ha iniciado sesión en la aplicación |
| **Proyecto** | AUTO_FRONT_SCREENPLAY |
| **Tipo de prueba** | E2E Front (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que el administrador ha iniciado sesión y navega al catálogo
Cuando completa el formulario con datos válidos del producto
Entonces debería visualizar el mensaje de producto creado exitosamente
```

**Resultado esperado:** Mensaje de confirmación visible en la UI.


---

## Escenario 14 — TC-HU01-21: E2E API — Crear producto vía API

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar creación de producto directamente vía API REST |
| **Técnica** | Prueba E2E — API con autenticación |
| **Prioridad** | Alta |
| **Precondiciones** | Usuario registrado y autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para gestionar el catálogo
Y el usuario del catálogo se autentica con credenciales válidas
Cuando el usuario crea un producto "Café Americano" de tipo "DRINK" en categoría "Bebidas Calientes" con precio 3.50
Entonces el producto debe crearse exitosamente con status 201
```

**Resultado esperado:** El producto se crea exitosamente y el sistema confirma el registro.


---

# HU-02: Editar Producto

## Escenario 15 — TC-HU02-01: Edición exitosa de producto

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un producto existente se actualiza correctamente |
| **Técnica** | Partición de equivalencia — datos válidos |
| **Prioridad** | Alta |
| **Precondiciones** | Producto existente en repositorio |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe un producto "Pizza" con precio 10
When el administrador modifica el nombre a "Pasta", la descripción a "Creamy" y el precio a 15
Then el producto actualizado tiene nombre "Pasta", descripción "Creamy" y precio 15
And el identificador del producto se mantiene igual al original
And los cambios quedan almacenados en el sistema
```

**Resultado esperado:** El producto se actualiza correctamente con los nuevos datos y su identificador permanece sin cambio.


---

## Escenario 16 — TC-HU02-02: Edición de producto inexistente lanza excepción

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que editar un producto inexistente lanza error claro |
| **Técnica** | Prueba negativa — recurso no encontrado |
| **Prioridad** | Alta |
| **Precondiciones** | UUID no existe en repositorio |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no existe un producto con el identificador proporcionado
When el administrador intenta actualizar ese producto
Then el sistema informa que el producto no fue encontrado
And no se realiza ninguna modificación
```

**Resultado esperado:** El sistema informa que el producto no existe y no se realiza ningún cambio.


---

## Escenario 17 — TC-HU02-18: E2E API — Actualizar producto existente

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar actualización completa de producto vía API REST |
| **Técnica** | Prueba E2E — API |
| **Prioridad** | Alta |
| **Precondiciones** | Producto previamente creado; usuario autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para gestionar el catálogo
Y el usuario del catálogo se autentica con credenciales válidas
Cuando el usuario crea un producto "Sopa del Día" de tipo "HOT_DISH" en categoría "Platos Calientes" con precio 5.00
Y el usuario actualiza el producto con nombre "Sopa Especial" de tipo "HOT_DISH" en categoría "Platos Calientes" con precio 6.50
Entonces el producto debe actualizarse con status 200
```

**Resultado esperado:** El producto se actualiza correctamente y el sistema confirma los cambios.


---

# HU-03: Desactivar Producto

## Escenario 18 — TC-HU03-01: Desactivar producto ACTIVE → INACTIVE

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un producto activo se desactiva correctamente (soft delete) |
| **Técnica** | Prueba basada en estado — transición ACTIVE→INACTIVE |
| **Prioridad** | Alta |
| **Precondiciones** | Producto ACTIVE en repositorio |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe un producto activo "Pizza"
When el administrador solicita desactivar el producto "Pizza"
Then el producto cambia su estado a Inactivo
And el cambio de estado queda registrado en el sistema
```

**Resultado esperado:** El producto pasa a estado Inactivo y el cambio queda almacenado correctamente.


---

## Escenario 19 — TC-HU03-04: Dominio: deactivate ya inactivo lanza excepción

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que desactivar un producto ya inactivo es rechazado |
| **Técnica** | Prueba basada en estado — transición inválida |
| **Prioridad** | Media |
| **Precondiciones** | Producto ya en estado INACTIVE |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que un producto ya está en estado Inactivo
When el administrador intenta desactivarlo nuevamente
Then el sistema rechaza la operación indicando que el producto ya se encuentra inactivo
```

**Resultado esperado:** El sistema rechaza la operación e informa que el producto ya está inactivo.


---

## Escenario 20 — TC-HU03-07: Integración: PATCH deactivate retorna INACTIVE

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar flujo completo de desactivación HTTP |
| **Técnica** | Prueba de integración — Spring Boot completo |
| **Prioridad** | Alta |
| **Precondiciones** | Producto creado vía POST; usuario autenticado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Integración (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el usuario crea un producto "ProductoDeactivar" exitosamente
When envía PATCH /api/products/{id}/deactivate
Then el sistema responde con status 200
And el body retorna status "INACTIVE"
```

**Resultado esperado:** El sistema confirma la desactivación y el producto aparece con estado Inactivo.


---

# HU-04: Visualizar Catálogo

## Escenario 21 — TC-HU04-01: Retorna solo productos ACTIVE

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el caso de uso retorna únicamente productos activos |
| **Técnica** | Partición de equivalencia — filtrado por estado |
| **Prioridad** | Alta |
| **Precondiciones** | Repositorio con productos ACTIVE y INACTIVE |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el sistema contiene 2 productos activos
When se consulta el catálogo de productos activos
Then el catálogo muestra exactamente 2 productos
And todos los productos mostrados están en estado Activo
```

**Resultado esperado:** El catálogo muestra únicamente los productos con estado Activo.


---

## Escenario 22 — TC-HU04-03: Filtrar por categoría retorna solo la categoría

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el filtrado por categoría funciona correctamente |
| **Técnica** | Partición de equivalencia — filtrado por categoría |
| **Prioridad** | Alta |
| **Precondiciones** | Productos de diferentes categorías en repositorio |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existen productos en categoría "Principales" y "Bebidas"
When se consulta el catálogo filtrando por categoría "Principales"
Then el catálogo muestra únicamente productos de la categoría "Principales"
And no se muestran productos de otras categorías
```

**Resultado esperado:** El catálogo filtra correctamente y muestra solo los productos de la categoría solicitada.


---

## Escenario 23 — TC-HU04-08: Frontend: solo productos ACTIVE visibles

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el hook useCatalog filtra productos inactivos en el frontend |
| **Técnica** | Prueba de componente — hook React |
| **Prioridad** | Alta |
| **Precondiciones** | productService mockeado retornando mezcla ACTIVE/INACTIVE |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el servicio retorna una mezcla de productos ACTIVE e INACTIVE
When el hook useCatalog carga los datos
Then solo los productos ACTIVE son expuestos
And el producto "Producto Inactivo" no aparece en la lista
```

**Resultado esperado:** El catálogo muestra solo los 2 productos activos; el producto inactivo no aparece en la lista.


---

## Escenario 24 — TC-HU04-19: E2E API — Producto desactivado no aparece

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar E2E vía API que un producto desactivado no aparece en el catálogo |
| **Técnica** | Prueba E2E — API |
| **Prioridad** | Alta |
| **Precondiciones** | Producto creado y desactivado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para gestionar el catálogo
Y el usuario del catálogo se autentica con credenciales válidas
Cuando el usuario crea un producto "Té Helado" de tipo "DRINK" en categoría "Bebidas" con precio 1.50
Y el usuario desactiva el producto creado del catálogo
Entonces el producto desactivado no debe aparecer en el listado del catálogo
```

**Resultado esperado:** El producto desactivado no aparece en el catálogo de productos disponibles.


---

## Escenario 25 — TC-HU04-21: Integración: Tiempo de respuesta del catálogo ≤ 2s

| Campo | Detalle |
|-------|--------|
| **Propósito** | Verificar que la consulta al catálogo responde en menos de 2 segundos, incluso con productos en la base de datos |
| **Técnica** | Prueba de rendimiento — valor límite de latencia |
| **Prioridad** | Media |
| **Precondiciones** | Al menos 10 productos creados en la base de datos; usuario autenticado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Integración (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existen al menos 10 productos registrados en el sistema
When se consulta el catálogo de productos activos
Then el sistema responde exitosamente
And el tiempo de respuesta es menor a 2000 milisegundos
```

**Resultado esperado:** El catálogo responde en menos de 2 segundos, cumpliendo el criterio de rendimiento CA-04-05.


---

# HU-05: Carga Masiva de Productos por CSV

## Escenario 26 — TC-HU05-01: Iniciar sesión de carga masiva

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que se puede iniciar una sesión de carga y se obtiene un ID válido |
| **Técnica** | Partición de equivalencia — flujo inicial |
| **Prioridad** | Alta |
| **Precondiciones** | Repositorio disponible |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
When el administrador inicia una sesión de carga masiva con el archivo "productos.csv"
Then el sistema crea una sesión de carga con un identificador único
And el nombre del archivo asociado es "productos.csv"
And la sesión queda registrada en el sistema
```

**Resultado esperado:** El sistema crea una sesión de carga masiva con identificador válido y el archivo queda asociado correctamente.


---

## Escenario 27 — TC-HU05-04: CSV con cabeceras inválidas lanza excepción

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un CSV con columnas incorrectas es rechazado |
| **Técnica** | Prueba negativa — estructura inválida |
| **Prioridad** | Alta |
| **Precondiciones** | Sesión de carga iniciada; archivo CSV con cabeceras "col1,col2" |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe una sesión de carga activa
And el archivo CSV tiene cabeceras "col1,col2" en lugar de las esperadas
When el administrador finaliza y solicita el procesamiento de la carga
Then el sistema rechaza el archivo indicando que las cabeceras son incorrectas
```

**Resultado esperado:** El sistema rechaza el archivo e informa cuáles cabeceras son incorrectas o faltantes.


---

## Escenario 28 — TC-HU05-08: CSV válido crea productos y retorna contadores

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar el flujo exitoso de creación de productos desde CSV |
| **Técnica** | Partición de equivalencia — flujo completo exitoso |
| **Prioridad** | Alta |
| **Precondiciones** | Sesión de carga activa; CSV válido con 1 producto |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe una sesión de carga activa
And el CSV contiene "Coca Cola,5.50,Bebidas,BAR,Refresco,Activo"
When el administrador finaliza y solicita el procesamiento de la carga
Then el resumen indica 1 producto creado, 0 actualizados y 0 errores
And el producto queda registrado en el catálogo
```

**Resultado esperado:** El sistema procesa el CSV exitosamente, crea 1 producto y el resumen refleja 0 errores.


---

## Escenario 29 — TC-HU05-10: CSV con filas inválidas persiste errores y las omite

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que las filas con datos inválidos son reportadas como error y omitidas |
| **Técnica** | Prueba negativa — múltiples errores de validación |
| **Prioridad** | Alta |
| **Precondiciones** | CSV con 3 filas inválidas: nombre vacío, precio no numérico, estación inválida |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe una sesión de carga activa
And el CSV contiene 3 filas con errores: nombre vacío, precio no numérico y estación no reconocida
When el administrador finaliza y solicita el procesamiento de la carga
Then el resumen indica 0 productos creados, 0 actualizados y 3 errores
And los 3 registros inválidos quedan reportados con su motivo de rechazo
```

**Resultado esperado:** El sistema identifica los 3 registros inválidos, no crea ningún producto y genera el reporte de errores correspondiente.


---

## Escenario 30 — TC-HU05-17: Archivo > tamaño máximo lanza excepción

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un archivo ensamblado que excede el límite es rechazado |
| **Técnica** | Análisis de valores límite — tamaño > máximo |
| **Prioridad** | Alta |
| **Precondiciones** | El sistema tiene configurado un tamaño máximo de archivo de 1 byte (para forzar el rechazo) |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el límite máximo de archivo es 1 byte
And el archivo ensamblado supera dicho límite
When el administrador finaliza y solicita el procesamiento de la carga
Then el sistema rechaza el archivo indicando que supera el tamaño máximo permitido
```

**Resultado esperado:** El sistema rechaza el archivo indicando que supera el tamaño máximo permitido.


---

## Escenario 31 — TC-HU05-25: Frontend: Flujo completo upload exitoso

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar el hook useBulkUpload en flujo completo exitoso |
| **Técnica** | Prueba de componente — hook React |
| **Prioridad** | Alta |
| **Precondiciones** | Servicios mockeados; archivo CSV válido |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el servicio de carga masiva está disponible
When el hook useBulkUpload.upload() recibe un archivo CSV válido
Then el status transiciona a "completed"
And el progress llega a 100
And el summary contiene los contadores del procesamiento
And el error es null
```

**Resultado esperado:** La carga finaliza exitosamente, el progreso llega al 100% y se muestra el resumen con los contadores de productos procesados.


---

## Escenario 32 — TC-HU05-27: Frontend: Archivo > 10MB rechazado sin llamar API

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el frontend NO llama al backend cuando el archivo supera 10MB |
| **Técnica** | Análisis de valores límite — validación client-side |
| **Prioridad** | Alta |
| **Precondiciones** | Archivo de 11MB generado en test |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el archivo CSV tiene 11MB de tamaño
When el hook useBulkUpload.upload() recibe el archivo
Then el status es "error"
And el mensaje de error contiene "10 MB"
And el sistema no inicia ninguna sesión de carga
And no se realiza la validación de cabeceras del archivo
```

**Resultado esperado:** El sistema muestra un mensaje de error indicando que el archivo supera los 10 MB y no se inicia ninguna carga al servidor.


---

## Escenario 33 — TC-HU05-36: E2E Front Screenplay — Carga CSV exitosa

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar E2E la carga de CSV válido desde la interfaz de usuario |
| **Técnica** | Prueba E2E — patrón Screenplay |
| **Prioridad** | Alta |
| **Precondiciones** | El administrador ha iniciado sesión |
| **Proyecto** | AUTO_FRONT_SCREENPLAY |
| **Tipo de prueba** | E2E Front (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que el administrador ha iniciado sesión y navega a carga masiva
Cuando sube un archivo CSV válido con productos
Entonces debería visualizar el resumen de la carga con los productos procesados
```

**Resultado esperado:** Resumen de carga visible en la UI.


---

## Escenario 34 — TC-HU05-48: E2E API — Flujo completo carga masiva

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar flujo completo de carga masiva vía API REST |
| **Técnica** | Prueba E2E — API |
| **Prioridad** | Alta |
| **Precondiciones** | Usuario registrado y autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario inicia una sesión de carga masiva
Y el usuario sube el contenido CSV al chunk 0
Y el usuario completa la sesión de carga
Entonces el sistema debe retornar el resumen del procesamiento con status 202
```

**Resultado esperado:** El sistema acepta la carga y retorna el resumen del procesamiento con los contadores de productos creados y errores.


---

## Escenario 35 — TC-HU05-49: E2E API — Descargar plantilla CSV

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el endpoint de descarga de plantilla retorna un archivo CSV válido |
| **Técnica** | Prueba E2E — API |
| **Prioridad** | Media |
| **Precondiciones** | Usuario registrado y autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario descarga la plantilla de carga masiva
Entonces el sistema debe retornar la plantilla CSV con status 200
```

**Resultado esperado:** El sistema retorna un archivo CSV descargable con las cabeceras esperadas: nombre, precio, categoría, estación, descripción y estado.


---

## Escenario 36 — TC-HU05-50: E2E API — Chunk > tamaño máximo retorna 413

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el sistema rechaza chunks que superan el tamaño máximo permitido |
| **Técnica** | Análisis de valores límite — tamaño > máximo vía API |
| **Prioridad** | Alta |
| **Precondiciones** | Sesión de carga masiva iniciada; usuario autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario inicia una sesión de carga masiva
Y el usuario sube un chunk que supera el tamaño máximo permitido
Entonces el sistema debe rechazar el archivo con status 413
```

**Resultado esperado:** El sistema rechaza el chunk indicando que supera el tamaño máximo permitido.


---

## Escenario 37 — TC-HU05-51: E2E API — CSV cabeceras incorrectas rechazado

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que un CSV con cabeceras no válidas es rechazado al completar la sesión |
| **Técnica** | Prueba negativa — estructura de archivo inválida vía API |
| **Prioridad** | Alta |
| **Precondiciones** | Sesión de carga masiva iniciada; usuario autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario inicia una sesión de carga masiva
Y el usuario sube un CSV con cabeceras incorrectas al chunk 0
Y el usuario completa la sesión de carga
Entonces el sistema debe rechazar el CSV con un error de validación
```

**Resultado esperado:** Error de validación indicando las cabeceras incorrectas o faltantes.


---

## Escenario 38 — TC-HU05-52: E2E API — Endpoint errores disponible

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que tras una carga con errores, el endpoint de errores retorna los registros inválidos |
| **Técnica** | Prueba funcional — disponibilidad de reporte de errores |
| **Prioridad** | Alta |
| **Precondiciones** | Carga masiva completada con al menos un registro inválido; usuario autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario inicia una sesión de carga masiva
Y el usuario sube el contenido CSV al chunk 0
Y el usuario completa la sesión de carga
Entonces el sistema debe exponer el endpoint de errores con status 200
```

**Resultado esperado:** El sistema expone el reporte de errores en formato CSV descargable.


---

## Escenario 39 — TC-HU05-53: E2E API — Productos cargados en catálogo

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que los productos insertados vía carga masiva aparecen en el catálogo |
| **Técnica** | Prueba E2E — verificación de visibilidad post-carga |
| **Prioridad** | Alta |
| **Precondiciones** | Carga masiva completada exitosamente; usuario autenticado |
| **Proyecto** | AUTO_API_SCREENPLAY |
| **Tipo de prueba** | E2E API (Screenplay) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Dado que se registra un nuevo usuario para carga masiva
Y el usuario de carga masiva se autentica con credenciales válidas
Cuando el usuario inicia una sesión de carga masiva
Y el usuario sube el contenido CSV al chunk 0
Y el usuario completa la sesión de carga
Entonces los productos cargados deben aparecer en el listado del catálogo
```

**Resultado esperado:** Los productos cargados desde el CSV aparecen en el catálogo con estado Activo.


---

## Escenario 40 — TC-HU05-54: Integración: Carga masiva no bloquea consultas al catálogo

| Campo | Detalle |
|-------|--------|
| **Propósito** | Verificar que las operaciones de carga masiva no bloquean las consultas concurrentes al catálogo de productos |
| **Técnica** | Prueba de concurrencia — simulación de operaciones paralelas |
| **Prioridad** | Media |
| **Precondiciones** | Al menos 1 producto creado; usuario autenticado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Integración (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existe al menos un producto en el catálogo
When se inician sesiones de carga masiva simultáneamente con consultas al catálogo
Then las consultas al catálogo responden en menos de 2 segundos
And las sesiones de carga masiva se inician correctamente
And ninguna operación queda bloqueada por la otra
```

**Resultado esperado:** Las consultas al catálogo no se bloquean ni degradan por las operaciones concurrentes de carga masiva, cumpliendo el criterio CA-05-04.


---

## Escenario 41 — TC-HU04-22: Búsqueda por nombre retorna productos coincidentes

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el caso de uso devuelve productos cuyo nombre contiene el término de búsqueda |
| **Técnica** | Prueba unitaria — mock del repositorio |
| **Prioridad** | Alta |
| **Precondiciones** | Repositorio con al menos un producto activo cuyo nombre coincida parcialmente |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que existen productos activos con nombre "Pizza Margherita" en el catálogo
When se busca por nombre con el término "pizza"
Then el sistema retorna los productos cuyo nombre contiene "pizza" sin importar mayúsculas
And el repositorio fue consultado con el método de búsqueda por nombre
```

**Resultado esperado:** Se retorna una lista con los productos activos cuyo nombre contiene el término buscado (case-insensitive).


---

## Escenario 42 — TC-HU04-23: Búsqueda por nombre sin coincidencias retorna lista vacía

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que la búsqueda por nombre retorna lista vacía cuando ningún producto coincide |
| **Técnica** | Prueba unitaria — mock del repositorio |
| **Prioridad** | Media |
| **Precondiciones** | Ningún producto con nombre que contenga el término buscado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no existen productos activos con nombre que contenga "sushi"
When se busca por nombre con el término "sushi"
Then el sistema retorna una lista vacía
And el repositorio fue consultado correctamente
```

**Resultado esperado:** Se retorna una lista vacía sin errores.


---

## Escenario 43 — TC-HU04-24: Adaptador: búsqueda por nombre delega correctamente al JPA

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el adaptador de repositorio delega la búsqueda por nombre al repositorio JPA con los parámetros correctos |
| **Técnica** | Prueba unitaria — mock del JPA repository |
| **Prioridad** | Media |
| **Precondiciones** | Repositorio JPA disponible |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el repositorio JPA tiene productos con nombre que contiene "pizza"
When el adaptador busca productos activos por nombre "pizza"
Then se invoca el método del JPA con estado ACTIVE y nombre "pizza"
And el resultado se mapea correctamente al modelo de dominio
```

**Resultado esperado:** El adaptador invoca el repositorio JPA con los parámetros correctos y retorna los productos mapeados.


---

## Escenario 44 — TC-HU04-25: Adaptador: búsqueda sin coincidencias retorna lista vacía

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el adaptador retorna lista vacía cuando no hay coincidencias por nombre |
| **Técnica** | Prueba unitaria — mock del JPA repository |
| **Prioridad** | Baja |
| **Precondiciones** | Ningún producto con nombre que contenga el término buscado |
| **Proyecto** | FoodTech-Kitchen-Services |
| **Tipo de prueba** | Unitaria — Caso de Uso (Backend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que no hay productos activos con nombre que contenga "sushi"
When el adaptador busca productos activos por nombre "sushi"
Then el resultado es una lista vacía
And se verificó la invocación correcta al repositorio JPA
```

**Resultado esperado:** Lista vacía retornada sin errores.


---

## Escenario 45 — TC-HU04-26: Frontend: búsqueda por nombre filtra productos en vista mesero

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que el hook useCatalog filtra los productos por nombre cuando se ingresa un término de búsqueda |
| **Técnica** | Prueba de componente — hook con mock del servicio |
| **Prioridad** | Alta |
| **Precondiciones** | Catálogo cargado con productos activos |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente — Hook (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el catálogo tiene productos "Gin Tonic" y "Risotto de Trufa"
When el mesero escribe "gin" en el campo de búsqueda
Then solo se muestra el producto "Gin Tonic"
And los demás productos quedan ocultos
```

**Resultado esperado:** Solo se muestran los productos cuyo nombre contiene el término buscado.


---

## Escenario 46 — TC-HU04-27: Frontend: búsqueda por nombre filtra productos en vista admin

| Campo | Detalle |
|-------|---------|
| **Propósito** | Verificar que la vista de administración filtra los productos del catálogo al escribir en el campo de búsqueda |
| **Técnica** | Prueba de componente — filtrado client-side |
| **Prioridad** | Alta |
| **Precondiciones** | Catálogo cargado con productos |
| **Proyecto** | FoodTech-Front |
| **Tipo de prueba** | Componente — Vista (Frontend) |
| **Implementado** | Sí |
| **Estado** | Cerrado |

```gherkin
Given que el administrador ve el catálogo con múltiples productos
When escribe un nombre parcial en el campo de búsqueda
Then la lista se filtra mostrando solo los productos cuyo nombre coincide
And si no hay coincidencias se muestra un mensaje indicando que no se encontraron productos
```

**Resultado esperado:** La lista de productos se actualiza en tiempo real mostrando solo las coincidencias por nombre.


---

# Resumen General

## Conteo de Test Cases por Proyecto y HU

| Historia | Kitchen-Services (Backend) | FoodTech-Front | AUTO_FRONT_SCREENPLAY | AUTO_FRONT_POM_FACTORY | AUTO_API_SCREENPLAY | Total |
|----------|:-:|:-:|:-:|:-:|:-:|:-:|
| **HU-01** | 13 | 3 | 2 | 2 | 2 | **22** |
| **HU-02** | 10 | 3 | 2 | 2 | 2 | **19** |
| **HU-03** | 9 | 2 | 1 | 1 | 1 | **14** |
| **HU-04** | 12 | 8 | 2 | 2 | 3 | **27** |
| **HU-05** | 25 | 10 | 6 | 6 | 7 | **54** |
| **Total** | **69** | **26** | **13** | **13** | **15** | **136** |

## Cobertura de Criterios de Aceptación

| Historia | CAs Definidos | CAs Cubiertos | CAs con Cobertura Parcial | CAs Sin Test |
|----------|:-:|:-:|:-:|:-:|
| HU-01 | 4 | **4/4** | 0 | 0 |
| HU-02 | 7 | **7/7** | 0 | 0 |
| HU-03 | 6 | **6/6** | 0 | 0 |
| HU-04 | 5 | **5/5** | 0 | 0 |
| HU-05 | 9 | **9/9** | 0 | 0 |
| **Total** | **31** | **31** | **0** | **0** |

---

## Distribución por Tipo de Prueba (por Test Suite)

| Test Suite | Tipo de Prueba | Total Tests | % |
|:----------:|----------------|:-:|:-:|
| TS-01 | Unit — Casos de Uso (Backend) | 37 | 27.2% |
| TS-02 | Unit — Modelo de Dominio (Backend) | 14 | 10.8% |
| TS-03 | Integración — Controllers REST (Backend) | 18 | 13.8% |
| TS-04 | Componente — Frontend (Hooks, Services, Componentes) | 26 | 19.1% |
| TS-05 | E2E — Front Screenplay | 13 | 10.0% |
| TS-06 | E2E — Front POM/Factory | 13 | 10.0% |
| TS-07 | E2E — API Screenplay | 15 | 11.5% |
| | **TOTAL** | **136** | **100%** |
