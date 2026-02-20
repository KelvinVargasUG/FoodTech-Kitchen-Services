# INTENCION ARQUITECTONICA

## Estructura de Clean Architecture

- domain/
  - Reglas de negocio centrales, entidades, objetos de valor, servicios de dominio.
- application/
  - Casos de uso, puertos de entrada, puertos de salida.
- infrastructure/
  - Adaptadores (web, persistencia, mensajeria), cableado de framework.

## Reglas de Importacion

- domain no debe importar Spring, Jackson, Reactor, JPA.
- application no debe depender de infrastructure.
- infrastructure puede depender de todas las capas.

## Patrones Obligatorios

- Repository Pattern mediante puertos de dominio (application/ports/out).
- Strategy Pattern para comportamiento variable (sin switch-case extensos en domain/services).

## Decision Arquitectonica

// ARCHITECTURE_DECISION:
This interface exists to decouple persistence and enable mocking in unit tests.

## Por Que Refactorizamos

Este refactor alinea el codigo base con la Definicion de Terminado (DoD) del
taller, reforzando los limites entre capas, reduciendo el acoplamiento con
frameworks en la logica central, y mejorando la testabilidad unitaria sin
cambiar el comportamiento en produccion.
