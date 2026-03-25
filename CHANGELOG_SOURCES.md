## 📅 Día 1 - 25/Mar/2026

### 🔹 Feature en análisis
Product Catalog Management
---

### 🤖 Propuesta de la IA
La IA sugirió construir un documento de diseño basado en las siguientes secciones:

1. **La feature**: qué hace, para qué sirve, qué problema resuelve.
2. **Requerimientos** *(obligatorio)*: usando Historias de Usuario (`"Como usuario quiero X para lograr Y"`) o Requerimientos Funcionales.
3. **Diseño de la solución**: arquitectura, componentes involucrados y flujo de datos.
4. **Diagramas** *(obligatorio)*: al menos diagrama de arquitectura + diagrama de secuencia o de clases.
5. **Pensamiento técnico** *(clave)*: decisiones de diseño, justificación de la solución elegida y consideraciones de escala/validaciones.

---

### 📚 Investigación humana (Documentación oficial)
- Fuente 1: [IEEE 830 - Video explicativo](https://www.youtube.com/watch?v=AotyBHVKp8I)
- Fuente 2: [IEEE 830 - PDF oficial UCM](https://www.fdi.ucm.es/profesor/gmendez/docs/is0809/ieee830.pdf)

**Hallazgos:**
- IEEE 830 define un estándar formal llamado **Software Requirements Specification (SRS)** con una estructura clara y estandarizada.
- Evita secciones sueltas o documentos inconsistentes al imponer una organización específica: introducción, descripción general, requerimientos específicos (funcionales, no funcionales, restricciones), etc.
- A diferencia de la propuesta libre de la IA, IEEE 830 provee un marco reconocido por la industria que facilita la trazabilidad, revisión y mantenimiento de los requerimientos.

---

### ⚖️ Análisis crítico
| Criterio | Propuesta IA | IEEE 830 |
|---|---|---|
| Estructura | Flexible, orientada a puntos de evaluación | Estandarizada, con secciones predefinidas |
| Trazabilidad | Depende del autor | Garantizada por el estándar |
| Cobertura de reqs. no funcionales | No explícita | Sección dedicada |
| Reconocimiento industria | No | Sí (estándar internacional) |
| Facilidad de inicio | Alta | Media (requiere conocer el estándar) |

**Problemas encontrados en la propuesta de la IA:** no establece cómo organizar requerimientos no funcionales ni restricciones del sistema; la estructura libre puede derivar en documentos difíciles de mantener o auditar.

---

### ✅ Decisión tomada
- Seguir el estándar **IEEE 830 (SRS)** para documentar la feature *Product Catalog Management*.
- **Justificación técnica:** provee una estructura clara, completa y reconocida; obliga a cubrir requerimientos funcionales, no funcionales y restricciones de forma explícita.
- **Justificación de negocio:** facilita la revisión por parte de otros stakeholders y garantiza trazabilidad de los requerimientos a lo largo del ciclo de vida del proyecto.

---

### 🧩 Impacto en el diseño
- El documento SRS se convierte en la fuente de verdad de la feature antes de entrar a diseño técnico.
- Las historias de usuario se derivan/mapean desde los requerimientos funcionales del SRS, no al revés.
- Las decisiones de arquitectura deberán justificarse contra las restricciones y reqs. no funcionales definidos en el SRS.

---
