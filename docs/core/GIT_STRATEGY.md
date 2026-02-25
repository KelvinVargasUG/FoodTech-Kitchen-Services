# GIT_STRATEGY (Gobernanza de Git)

Este documento define la estrategia transversal de trabajo con Git para este repositorio. Su propósito es asegurar trazabilidad, cambios revisables, y evolución compatible con la arquitectura.

Alcance: aplica a cualquier contribución futura (documentación, pruebas, refactors y código). No describe features específicos.

Compatibilidad: esta estrategia debe operar en coherencia con `docs/core/ARCHITECTURE_CONTEXT.md`.

---

## 1) Modelo de ramas

- `develop` como línea base estable
  - Debe mantenerse en estado “mergeable”: build/test verdes.
  - Se integra trabajo mediante PR/merge con revisión.

- `feature/*` para cambios funcionales
  - Una feature por rama.
  - Mantener alcance acotado.

- Trabajo directo en `main`
  - Prohibido si `main` existe y representa producción/release.
  - Si el repo no usa `main` como release, el equivalente “release line” debe recibir el mismo trato (sin commits directos).

---

## 2) Convención de commits

Formato de prefijo obligatorio (estilo “type:”):
- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `refactor:` refactor sin cambio funcional
- `test:` cambios de pruebas
- `docs:` cambios de documentación
- `chore:` tareas de mantenimiento/infra menor

Regla clave: separación estricta de intenciones
- Movimientos estructurales (renombres/reubicaciones) deben ir en commits separados de cambios funcionales.
- Cambios de documentación transversal (docs/core) no deben mezclarse con lógica productiva.

---

## 3) Evidencia de diseño incremental

- Commits pequeños
  - Cada commit debe representar una unidad coherente de cambio.
  - Evitar commits “mega” con múltiples intenciones.

- Separación entre etapas
  - Diseño/decisión (documentación) → implementación → limpieza.
  - Cada etapa debe poder revisarse aisladamente.

- No mezclar documentación estructural con código productivo
  - `docs/core` es gobernanza transversal; su historia debe permanecer clara.

---

## 4) Política de documentación

- `docs/core/`
  - Documentación transversal y normativa.
  - Debe permanecer libre de referencias a esfuerzos temporales.

- `docs/workshops/`
  - Documentación histórica y de auditoría (evidencia de entregables/iteraciones).
  - No debe contaminar `docs/core`.

- Duplicados
  - Ningún duplicado permitido.
  - Si existen dos variantes del mismo documento, debe definirse una única fuente oficial y eliminar el resto (manteniendo historial mediante git).

- Idioma canónico
  - Un solo idioma canónico para documentación.
  - Nombres técnicos y código se mantienen en inglés (identificadores/clases/paquetes).

---

## 5) Política de merges

Requisitos previos para merge a `develop`:
- Revisión previa (mínimo una revisión humana o equivalente definido por el equipo).
- Working tree limpio.
- Tests/build en verde.
- No introducir violaciones de arquitectura.

---

## 6) Regla de trazabilidad

- Cambios arquitectónicos
  - Cualquier cambio de boundaries, ubicación de puertos, dependencias permitidas, o políticas transversales debe reflejarse en `docs/core/ARCHITECTURE_CONTEXT.md`.

- Cambios de dominio
  - Cualquier cambio en entidades, estados, invariantes o servicios de dominio debe reflejarse en `docs/core/DOMAIN_MAP.md`.

- Excepciones
  - Cualquier excepción a reglas arquitectónicas debe quedar documentada como decisión activa en el documento normativo correspondiente.

---

Fin.
