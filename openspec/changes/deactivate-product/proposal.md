# Proposal: HU-03: Desactivar producto del catálogo (Borrado Lógico)

## Intent
Permitir a los administradores desactivar (borrado lógico) productos del catálogo para evitar que sean seleccionados en nuevos pedidos, preservando el registro histórico para facturación, auditoría y pedidos en curso.

## Scope

### In Scope
- Endpoint en el Backend para cambiar el estado de un producto a Inactivo/Activo.
- Modificación en la UI (Administrador) para incluir acción rápida de "Desactivar/Activar" desde el listado.
- Filtrado de productos inactivos en la vista de toma de pedidos para Meseros.
- Asegurar que la información del producto se mantenga en pedidos históricos (COMPLETED/INVOICED).
- Garantizar que los pedidos en curso (CREATED/IN_PROGRESS) no se vean afectados.

### Out of Scope
- Eliminación física (Hard Delete) de productos de la base de datos.
- Modificaciones a la estructura de la base de datos (se usa el campo de estado existente).

## Approach
Implementar los casos de uso para activar y desactivar productos en `ProductController`. El frontend deberá llamar a estos endpoints desde el listado de productos usando botones de acción rápida. En la vista de los meseros, la consulta de productos disponibles debe filtrar por `status == ACTIVE`. El diseño actual del historial y persistencia de `OrderProduct` ya soporta preservación de datos (Soft Delete), por lo cual esta capacidad se probará y garantizará.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `com.foodtech.kitchen.application.usecases.*` | New | Casos de uso de Desactivación y Activación |
| `com.foodtech.kitchen.infrastructure.rest.ProductController` | Modified | Nuevos endpoints de activación/desactivación |
| `com.foodtech.kitchen.domain.model.Product` | Modified | Método para enmascarar acción de cambio de estado |
| `FoodTech-Front/src/services/productService.ts` | Modified | Llamadas API para (des)activar y obtener solo activos |
| `FoodTech-Front/src/components/admin/` | Modified | Botones de (des)activar en catálogo |
| `FoodTech-Front/src/components/waiter/` | Modified | Filtro de solo productos activos |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Inconsistencias en pedidos en curso al desactivar | Low | Comprobar y asegurar tests de integración para ciclo de vida de Order vs Product inactivo |
| Filtrado tardío en Frontend vs Backend | Medium | Asegurar que el endpoint usado por Meseros retorne ya la lista filtrada desde Backend (`/api/products?status=ACTIVE`) |

## Rollback Plan
1. Revertir los endpoints expuestos mediante control de versiones en git.
2. Desplegar versión previa limpia del Frontend si hay problemas en producción.
3. Se trata de una actualización segura en código dado que no toca el esquema de la BD.

## Dependencies
- Backend (FoodTech-Kitchen-Services): Endpoints REST y Spring Boot
- Frontend (FoodTech-Front): React UI

## Success Criteria

- [ ] (CA1) Administrador puede desactivar producto y el estado cambia a Inactivo con mensaje de éxito.
- [ ] (CA2) Producto inactivo no es devuelto/listado a Meseros.
- [ ] (CA3) Pedidos completados preservan la información de productos inactivos.
- [ ] (CA4) Pedidos en curso prosiguen su flujo sin error.
- [ ] (CA5) Administrador puede reactivar el producto y éste vuelve a estar disponible para Meseros.