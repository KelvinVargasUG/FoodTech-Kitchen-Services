# COPILOT_CONTEXT (Gobernanza IA-First)

Este documento define reglas transversales para el uso de asistentes de IA en este repositorio. Su objetivo es mejorar la calidad, trazabilidad y seguridad de los cambios, sin degradar la arquitectura ni introducir deuda accidental.

Alcance: aplica a cualquier contribución futura (diseño, documentación, pruebas, refactors y cambios de código), independientemente del tipo de feature.

Relación con la arquitectura: cualquier sugerencia, cambio o refactor propuesto o implementado con ayuda de IA debe respetar estrictamente `docs/core/ARCHITECTURE_CONTEXT.md`. Si una recomendación entra en conflicto con ese documento, se debe detener y registrar la excepción como una decisión arquitectónica activa antes de continuar.

---

## 1) Principios IA-First del repositorio

1. Diseño antes de implementación
   - Primero se entiende el problema y el impacto en capas/puertos/adaptadores.
   - Luego se propone una solución mínima alineada a la arquitectura.

2. Verificación antes de modificación
   - No se asume la existencia de clases, paquetes o dependencias.
   - Toda acción debe estar precedida por verificación (búsqueda/lectura/árbol del repo).

3. Documentación estructural se crea directamente en el repo
   - La documentación transversal (en `docs/core/`) debe escribirse como archivos versionados.
   - Evitar procesos manuales de “copy-paste” fuera del control de versiones.

4. Cambios pequeños y reversibles
   - Preferir incrementos pequeños con validaciones intermedias.
   - Evitar refactors masivos no justificados.

---

## 2) Uso recomendado de modelos (guía general)

Esta guía es intencionalmente tecnológica-neutral: selecciona el tipo de modelo según la naturaleza del trabajo.

- Modelos de razonamiento profundo
  - Uso: análisis arquitectónico, diagnóstico de deuda estructural, diseño de boundaries, evaluación de riesgos.
  - Expectativa: respuestas justificadas, explícitas en supuestos y con verificación del estado real del repo.

- Modelos orientados a código
  - Uso: implementación controlada, refactors acotados, cambios en wiring/config, ajustes de pruebas.
  - Expectativa: cambios mínimos, consistentes con el estilo del repo y con validación (tests/build).

- Modelos ligeros
  - Uso: tareas repetitivas/operativas (formatos, renombres, reorganización documental, mantenimiento menor).
  - Expectativa: precisión de rutas/comandos y bajo riesgo.

---

## 3) Flujo estándar para cualquier feature

1. Análisis estructural
   - Identificar capa(s) impactadas (domain/application/infrastructure).
   - Identificar puertos existentes y/o necesidad de nuevos.
   - Detectar riesgos de acoplamiento o violación de reglas.

2. Diseño técnico
   - Definir responsabilidades por clase.
   - Definir contratos (interfaces/puertos) antes de implementar adaptadores.

3. Pruebas primero (cuando aplique)
   - Si el cambio afecta lógica en application/domain, priorizar pruebas unitarias.
   - Si el cambio afecta infraestructura (web/persistencia), priorizar pruebas de integración o validaciones equivalentes.

4. Implementación mínima
   - Implementar la mínima superficie necesaria.
   - Evitar “mejoras colaterales” no solicitadas.

5. Refactor seguro
   - Refactor solo después de que haya verificación (tests/build) y con cambios pequeños.
   - Mantener commits separados cuando se mezclen intenciones (p. ej. movimientos vs cambios funcionales).

---

## 4) Patrón obligatorio de prompts (estructura de trabajo)

Toda solicitud de cambio debe seguir estas fases:

Fase A — Verificación
- Confirmar rutas y existencia de archivos/clases.
- Confirmar dependencias y wiring relevantes.
- Identificar el punto exacto de cambio (archivos involucrados).

Fase B — Acción condicional
- Ejecutar cambios solo si la verificación confirma el estado esperado.
- Si hay ambigüedad o contradicción, detener y pedir aclaración o registrar decisión.

Fase C — Validación final
- Verificar `git status`.
- Ejecutar tests/build relevantes o validaciones equivalentes.
- Confirmar que no se introdujeron violaciones de arquitectura.

---

## 5) Prohibiciones

- No escribir pruebas después de implementar como “afterthought”.
  - Si el cambio lo requiere, se diseña la prueba como parte del cambio.

- No introducir frameworks en capas internas.
  - Prohibido acoplar domain/application a Spring/Reactor/Jackson/JPA.

- No realizar cambios estructurales sin documentarlos.
  - Cualquier cambio de boundaries, puertos, adapters, wiring o reglas debe reflejarse en `docs/core/ARCHITECTURE_CONTEXT.md`.

---

## 6) Relación con la arquitectura

- `docs/core/ARCHITECTURE_CONTEXT.md` es la autoridad normativa para:
  - dependencias permitidas,
  - ubicación de puertos,
  - prohibiciones de frameworks,
  - patrones de implementación de casos de uso y adaptadores,
  - manejo de excepciones.

- Cuando una sugerencia de IA implique una excepción, se debe:
  - detener el cambio,
  - proponer la excepción de forma explícita,
  - documentarla en “Decisiones Arquitectónicas Activas” (según la regla de excepciones del contexto arquitectónico),
  - recién entonces continuar.

---

Fin.
