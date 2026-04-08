# Plan de Pruebas — Product Catalog Management

| Campo    | Detalle          |
| -------- | ---------------- |
| Versión  | 2.0              |
| Fecha    | 07/04/2026       |
| Estado   | En revisión      |
| Proyecto | FoodTech Kitchen |
| Área     | QA               |

---

# 1. Introducción

## 1.1 Propósito

Este documento define la estrategia, alcance y criterios de calidad para validar la feature **Product Catalog Management**, la cual permite la gestión dinámica del catálogo de productos y la carga masiva mediante archivos CSV.

Su objetivo es garantizar la correcta funcionalidad del sistema, la integridad de datos y el cumplimiento de los requisitos funcionales y no funcionales.

---

## 1.2 Contexto

La feature permite:

- Gestión CRUD de productos
- Activación / desactivación (soft delete)
- Visualización del catálogo
- Carga masiva mediante CSV (hasta 10MB)

Esta funcionalidad impacta directamente en:

- Backend de gestión de productos
- Base de datos
- Frontend de catálogo
- Módulo de procesamiento de archivos

---

## 2. Alcance de las Pruebas

## 2.1 Funcionalidades en Alcance

| Épica                                    | Historia de Usuario | Descripción                              | Prioridad |
| ---------------------------------------- | ------------------- | ---------------------------------------- | --------- |
| **EPIC-01 — Gestión de Productos**       | HU-01               | Crear producto                           | Alta      |
|                                          | HU-02               | Editar producto                          | Alta      |
|                                          | HU-03               | Desactivar producto (soft delete)        | Alta      |
| **EPIC-02 — Visualización del Catálogo** | HU-04               | Visualizar catálogo de productos activos | Alta      |
| **EPIC-03 — Carga Masiva de Productos**  | HU-05               | Carga masiva de productos mediante CSV   | Alta      |

---

## 2.2 Fuera de Alcance

- Procesamiento de pagos
- Inventario en tiempo real
- Integraciones externas
- Autenticación

---

## 2.3 Tipos de Prueba

Se contemplan los siguientes tipos de pruebas para garantizar la calidad funcional y técnica del sistema:

- **Pruebas funcionales:**
  Orientadas a validar las operaciones del catálogo de productos, incluyendo la creación, edición, desactivación y visualización, así como las validaciones de reglas de negocio y campos obligatorios.

- **Pruebas de integración:**
  Enfocadas en verificar la correcta interacción entre los componentes del sistema, especialmente la persistencia en base de datos y el procesamiento de archivos CSV durante la carga masiva de productos.

- **Pruebas End-to-End (E2E):**
  Diseñadas para validar los flujos completos desde la perspectiva del usuario, asegurando que las operaciones del catálogo se ejecuten correctamente de principio a fin.

- **Pruebas no funcionales:**
  Destinadas a evaluar aspectos de rendimiento y comportamiento del sistema, incluyendo:
  - tiempos de respuesta en la consulta del catálogo,
  - procesamiento asincrónico de archivos CSV,
  - estabilidad del sistema durante operaciones de carga masiva.

---

# 3. Objetivos de Calidad

| Criterio                           | Descripción                                                                                                                                                                                           |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Cobertura funcional**            | Garantizar la validación del 100% de los criterios de aceptación definidos para las historias de usuario HU-01 a HU-05, cubriendo flujos principales, alternativos y escenarios de error.             |
| **Integridad de datos**            | Asegurar que la información del catálogo se mantenga consistente, evitando la creación de productos duplicados y garantizando la correcta persistencia de los registros en base de datos.             |
| **Consistencia del estado**        | Verificar que los productos mantengan un estado válido y coherente (Activo/Inactivo) en todo momento, reflejándose correctamente en el catálogo y en los flujos de negocio.                           |
| **Rendimiento del sistema**        | Validar que las consultas del catálogo respondan dentro de tiempos aceptables (≤ 2 segundos), garantizando una experiencia fluida para el usuario.                                                    |
| **Procesamiento de carga masiva**  | Confirmar que la carga de archivos CSV se ejecute de manera asincrónica, sin bloquear la operación general del sistema, incluso para archivos de gran tamaño (hasta 10 MB).                           |
| **Validación de datos de entrada** | Verificar que el sistema valide correctamente la estructura y contenido de los archivos CSV antes y durante su procesamiento, rechazando entradas inválidas y generando reportes de error detallados. |

---

## 4. Estrategia de Pruebas

### 4.1 Enfoque General

La estrategia de pruebas se basa en un enfoque progresivo, iniciando con la validación de la lógica de negocio y escalando hacia la verificación de la integración entre componentes y los flujos completos del sistema.

Se prioriza la validación de los escenarios críticos del negocio, tales como la gestión del catálogo y la carga masiva de productos, antes de abordar escenarios alternativos y condiciones excepcionales.

---

### 4.2 Niveles de Prueba

| Nivel       | Tipo de Prueba               | Descripción                                                                                                                                                                                                                                                           |
| ----------- | ---------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Nivel 1** | **Pruebas Funcionales**      | Validan el comportamiento del sistema a nivel de historias de usuario, asegurando el cumplimiento de los criterios de aceptación. Incluye operaciones como creación, edición, desactivación y visualización de productos, así como validaciones de reglas de negocio. |
| **Nivel 2** | **Pruebas de Integración**   | Verifican la correcta interacción entre los componentes del sistema, especialmente en la persistencia de datos y el procesamiento de archivos CSV durante la carga masiva de productos.                                                                               |
| **Nivel 3** | **Pruebas End-to-End (E2E)** | Evalúan los flujos completos desde la perspectiva del usuario, asegurando que las operaciones del sistema funcionen correctamente de principio a fin, sin inconsistencias.                                                                                            |
| **Nivel 4** | **Pruebas No Funcionales**   | Analizan aspectos de rendimiento y comportamiento del sistema, incluyendo el procesamiento asincrónico de archivos CSV, manejo de grandes volúmenes de datos y tiempos de respuesta del catálogo.                                                                     |

---

### 4.3 Técnicas de Prueba

Para garantizar una cobertura adecuada, se aplican las siguientes técnicas de diseño de pruebas:

- **Partición de equivalencia:** para validar diferentes conjuntos de datos de entrada (válidos e inválidos).
- **Análisis de valores límite:** aplicado a campos como precio y tamaño de archivos CSV.
- **Pruebas negativas:** orientadas a verificar el comportamiento del sistema ante entradas incorrectas o condiciones no válidas.
- **Pruebas basadas en estado:** para validar las transiciones de estado de los productos (Activo/Inactivo) y su impacto en el sistema.

---

# 5. Cobertura de Requerimientos

La cobertura de pruebas se define en función de las historias de usuario y sus criterios de aceptación, asegurando que cada funcionalidad sea validada mediante distintos tipos de prueba según su naturaleza.

| Historia de Usuario | Descripción         | Tipos de Prueba Aplicados | Enfoque de Validación                                                                                                      |
| ------------------- | ------------------- | ------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| **HU-01**           | Crear producto      | Funcional, Integración    | Validación de creación, persistencia en base de datos, control de duplicados y asignación de estado inicial.               |
| **HU-02**           | Editar producto     | Funcional, Integración    | Verificación de actualización de datos, validación de campos obligatorios y consistencia de la información en el catálogo. |
| **HU-03**           | Desactivar producto | Funcional                 | Validación de cambio de estado (activo/inactivo), ocultamiento en catálogo y preservación del historial (soft delete).     |
| **HU-04**           | Visualizar catálogo | Funcional, No funcional   | Validación de visualización de productos activos, manejo de catálogo vacío y tiempos de respuesta del sistema.             |
| **HU-05**           | Carga masiva CSV    | Integración, No funcional | Validación de estructura del archivo, procesamiento asincrónico, manejo de errores y consistencia de los datos cargados.   |

---

# 6. Flujos E2E

## Flujo E2E-01 — Creación y visualización de producto

| Campo | Detalle |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | El administrador tiene acceso al módulo de gestión de productos. |
| **Pasos**              | 1. El administrador accede al formulario de creación de producto.<br>2. Ingresa información válida (nombre, precio, categoría y estación).<br>3. Guarda el producto.<br>4. El sistema registra el producto en la base de datos.<br>5. El sistema actualiza el catálogo. |
| **Resultado Esperado** | El producto es creado exitosamente, se encuentra persistido en la base de datos y se visualiza inmediatamente en el catálogo con estado "Activo". |

## Flujo E2E-02 — Edición de producto

| Campo | Detalle |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | Existe al menos un producto registrado en el sistema y el administrador tiene acceso al módulo de gestión de productos.|
| **Pasos**              | 1. El administrador accede al panel de gestión del catálogo.<br>2. Selecciona un producto existente para editar.<br>3. El sistema muestra la información actual del producto.<br>4. El administrador modifica uno o más campos (nombre, precio, categoría, estación o estado).<br>5. Guarda los cambios.<br>6. El sistema valida los datos ingresados.<br>7. El sistema actualiza la información en la base de datos.<br>8. Se actualiza la vista del catálogo. |
| **Resultado Esperado** | Los cambios se aplican correctamente, el producto actualizado se refleja en el catálogo de manera inmediata y la información permanece consistente en el sistema. |

## Flujo E2E-03 — Desactivación de producto
| Campo | Detalle |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | Existe al menos un producto activo en el catálogo. |
| **Pasos**              | 1. El administrador accede al listado de productos.<br>2. Selecciona la opción de desactivar un producto activo.<br>3. El sistema actualiza el estado del producto a "Inactivo".<br>4. Se actualiza la vista del catálogo. |
| **Resultado Esperado** | El producto cambia su estado a "Inactivo", deja de mostrarse en el catálogo para nuevos pedidos y su información se mantiene disponible para consultas históricas. |


## Flujo E2E-04 — Carga masiva de productos mediante CSV
| Campo | Detalle |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Precondición**       | El administrador dispone de un archivo CSV válido con estructura correcta y tamaño menor a 10MB. |
| **Pasos**              | 1. El administrador accede a la sección de carga masiva.<br>2. Selecciona un archivo CSV y lo envía para procesamiento.<br>3. El sistema valida la estructura del archivo.<br>4. Se inicia el procesamiento en segundo plano (asincrónico).<br>5. El sistema procesa cada registro, creando o actualizando productos según corresponda.<br>6. Se genera un resumen del proceso. |
| **Resultado Esperado** | El sistema procesa el archivo sin bloquear la aplicación, registra correctamente los productos válidos, genera un reporte de errores para registros inválidos y muestra un resumen con los resultados de la carga. |

---

## 7. Criterios de Entrada y Salida

### 7.1 Criterios de Entrada

| ID    | Criterio                                                           |
| ----- | ------------------------------------------------------------------ |
| CE-01 | El ambiente de pruebas debe estar desplegado y operativo.          |
| CE-02 | La base de datos debe estar configurada y accesible.               |
| CE-03 | Los datos de prueba mínimos deben estar cargados.                  |
| CE-04 | Los endpoints y componentes de la feature deben estar disponibles. |
| CE-05 | Deben existir archivos CSV de prueba válidos e inválidos.          |
| CE-06 | La versión de la funcionalidad debe haber sido entregada a QA.     |

### 7.2 Criterios de Salida

| ID    | Criterio                                                                    |
| ----- | --------------------------------------------------------------------------- |
| CS-01 | El 100% de los casos de prueba de prioridad alta debe haber sido ejecutado. |
| CS-02 | No deben existir defectos críticos abiertos.                                |
| CS-03 | La carga masiva por CSV debe haber sido validada de extremo a extremo.      |
| CS-04 | Debe haberse verificado la consistencia funcional del catálogo.             |
| CS-05 | El reporte final de pruebas debe estar elaborado y revisado.                |

---

## 8. Ambiente de Pruebas y Configuración

### 8.1 Configuración del Ambiente

El ambiente de pruebas estará configurado para simular condiciones reales de operación del sistema, permitiendo la validación de las funcionalidades del catálogo de productos y la carga masiva mediante archivos CSV.

| Componente                    | Descripción                                                                                                                                | Responsable |
| ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ----------- |
| **Backend**                   | Servicio encargado de la lógica de negocio del catálogo de productos, incluyendo operaciones CRUD y procesamiento de archivos CSV.         | Dev         |
| **Base de datos**             | Sistema de almacenamiento persistente utilizado para la gestión de productos, categorías y registros de carga masiva.                      | Dev / DBA   |
| **Procesamiento de archivos** | Módulo encargado de ejecutar la carga masiva de productos mediante procesamiento asincrónico o por lotes, evitando el bloqueo del sistema. | Dev         |
| **Frontend**                  | Interfaz de usuario utilizada por administradores y meseros para interactuar con el catálogo de productos.                                 | Dev         |
| **Herramientas de prueba**    | Herramientas utilizadas por el equipo de QA para ejecutar pruebas manuales y validar endpoints del sistema.                                | QA          |

---

### 8.2 Datos de Prueba

Para la ejecución de las pruebas se deben considerar los siguientes datos:

* Productos activos e inactivos previamente registrados.
* Categorías válidas configuradas en el sistema.
* Usuarios con rol de administrador y mesero.
* Archivos CSV de prueba que incluyan:

  * datos válidos,
  * datos inválidos,
  * estructuras incorrectas,
  * archivos cercanos al límite de tamaño permitido (10 MB).

---

# 9. Riesgos y Mitigaciones

| ID       | Riesgo                                                                                                         | Probabilidad | Impacto | Mitigación                                                                                                                                        |
| -------- | -------------------------------------------------------------------------------------------------------------- | :----------: | :-----: | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R-01** | Archivos CSV con estructura inválida o datos inconsistentes que impidan su procesamiento correcto.             |     Alta     |   Alto  | Implementar validaciones tempranas de estructura y contenido del archivo antes de iniciar el procesamiento, mostrando mensajes claros al usuario. |
| **R-02** | Procesamiento de archivos CSV de gran tamaño que pueda afectar el rendimiento o disponibilidad del sistema.    |     Alta     |   Alto  | Utilizar procesamiento asincrónico o por lotes, evitando bloquear la aplicación y controlando el consumo de recursos.                             |
| **R-03** | Inserción de productos duplicados debido a validaciones insuficientes durante la creación o carga masiva.      |     Media    |   Alto  | Implementar validación de unicidad por nombre u otros atributos clave antes de persistir los registros.                                           |
| **R-04** | Inconsistencia en el estado de los productos (Activo/Inactivo) que afecte la visualización en el catálogo.     |     Media    |  Medio  | Validar correctamente las transiciones de estado y asegurar su correcta aplicación en consultas del catálogo.                                     |
| **R-05** | Fallos en el procesamiento de carga masiva que generen datos incompletos o inconsistentes.                     |     Media    |   Alto  | Implementar manejo de errores por registro, generación de reportes de errores y persistencia únicamente de datos válidos.                         |
| **R-06** | Dependencia del ambiente de pruebas (caídas, configuración incorrecta) que afecte la ejecución de las pruebas. |     Baja     |   Alto  | Validar el estado del ambiente antes de iniciar pruebas y coordinar con DevOps para asegurar su disponibilidad.                                   |

---

# 10. Cronograma

| Fase                                | Actividades                                                                                                                  | Duración Estimada | Responsable |
| ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- | :---------------: | ----------- |
| **Fase 1 — Preparación**            | Revisión de criterios de aceptación, diseño de casos de prueba, configuración del ambiente y preparación de datos de prueba. |       2 días      | QA Lead     |
| **Fase 2 — Ejecución Funcional**    | Ejecución de pruebas relacionadas con la gestión del catálogo (crear, editar, desactivar y visualizar productos).            |       3 días      | QA          |
| **Fase 3 — Pruebas de Integración** | Validación de persistencia en base de datos y procesamiento de carga masiva mediante archivos CSV.                           |       2 días      | QA + Dev    |
| **Fase 4 — Pruebas No Funcionales** | Evaluación de rendimiento del catálogo y comportamiento del sistema durante la carga masiva de archivos.                     |       2 días      | QA + Dev    |
| **Fase 5 — Cierre y Reporte**       | Retesting de defectos, validación final y generación del reporte de pruebas.                                                 |       1 día       | QA          |


---

# 11. Roles

| Rol                     | Perfil sugerido        | Responsabilidades                                                                                                                                                                                |
| ----------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **QA Lead**             | Analista QA Senior     | Definir y mantener el plan de pruebas. Diseñar la estrategia de testing. Supervisar la ejecución de pruebas. Priorizar defectos y coordinar con el equipo. Elaborar el reporte final de calidad. |
| **QA**                  | Analista QA            | Diseñar y ejecutar casos de prueba funcionales, de integración y E2E. Registrar defectos con evidencia. Realizar retesting y validar correcciones.                                               |
| **Desarrollador (Dev)** | Backend / Full Stack   | Brindar soporte técnico durante la ejecución de pruebas. Corregir defectos identificados. Asegurar la correcta integración de la funcionalidad en el ambiente de pruebas.                        |
| **Product Owner (PO)**  | Responsable de negocio | Validar los criterios de aceptación. Priorizar defectos según impacto en el negocio. Aprobar el cierre de pruebas.                                                                               |

---

# 12. Gestión de Defectos
## 12.1 Clasificación de Severidad

| Severidad        | Impacto                                                                               | Ejemplos en esta feature                                                                                                                           |
| ---------------- | ------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Crítico (S1)** | Bloquea completamente el flujo principal del sistema. No existe solución alternativa. | El sistema se bloquea durante la carga masiva de CSV. No se pueden crear productos. El catálogo no carga.                                          |
| **Alto (S2)**    | Afecta funcionalidades principales, pero existe una alternativa parcial.              | Se permiten productos duplicados. La carga CSV no procesa correctamente registros válidos. El estado activo/inactivo no se respeta en el catálogo. |
| **Medio (S3)**   | Impacta funcionalidades secundarias o genera inconsistencias menores.                 | Validaciones incorrectas en campos obligatorios. Mensajes de error poco claros. Resumen de carga incorrecto.                                       |
| **Bajo (S4)**    | Impacto mínimo, principalmente visual o de experiencia de usuario.                    | Problemas de formato en la interfaz, textos incorrectos o elementos desalineados.                                                                  |

---

### 12.2 Flujo de Gestión de Defectos

* Los defectos deben ser registrados en la herramienta de seguimiento del proyecto, incluyendo:

  * descripción del problema,
  * pasos para reproducir,
  * resultado esperado vs resultado actual,
  * evidencia (capturas, logs, etc.).
* Los defectos críticos y altos deben ser comunicados inmediatamente al equipo de desarrollo.
* Una vez corregido el defecto, el QA responsable debe ejecutar el **retesting** correspondiente.
* Un defecto se considera cerrado únicamente cuando la corrección ha sido validada en el ambiente de pruebas.

---

# 13. Métricas de Seguimiento

Las siguientes métricas permitirán evaluar el progreso, la calidad del sistema y la efectividad del proceso de pruebas durante la ejecución del Test Plan.
| Métrica                              | Descripción                                                                      | Objetivo / Referencia         |
| ------------------------------------ | -------------------------------------------------------------------------------- | ----------------------------- |
| **Cobertura de ejecución**           | Porcentaje de casos de prueba ejecutados respecto al total planificado.          | ≥ 95% de ejecución total      |
| **Tasa de éxito (Pass Rate)**        | Porcentaje de casos de prueba que pasan respecto a los ejecutados.               | ≥ 90% de pruebas exitosas     |
| **Densidad de defectos**             | Número de defectos identificados por historia de usuario o módulo.               | Monitoreo continuo            |
| **Defectos por severidad**           | Distribución de defectos según su severidad (Crítico, Alto, Medio, Bajo).        | 0 defectos críticos al cierre |
| **Tiempo de resolución de defectos** | Tiempo promedio desde el reporte de un defecto hasta su cierre.                  | Críticos ≤ 24h                |
| **Cobertura funcional**              | Porcentaje de criterios de aceptación validados mediante pruebas.                | 100% de cobertura             |
| **Reapertura de defectos**           | Porcentaje de defectos que vuelven a abrirse después de ser cerrados.            | ≤ 5%                          |
| **Efectividad de pruebas**           | Relación entre defectos encontrados en QA vs defectos encontrados en producción. | Maximizar detección en QA     |
