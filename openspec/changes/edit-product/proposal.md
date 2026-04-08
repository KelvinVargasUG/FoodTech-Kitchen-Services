# Proposal: HU-02: Editar producto existente

## Intent
Permitir a los administradores modificar la información de los productos existentes en el catálogo (nombre, descripción, precio, categoría, estación/tipo y estado), para asegurar que el menú esté siempre actualizado y no afectar órdenes previas en operaciones de cocina y facturación.

## Scope

### In Scope
- Endpoint PUT o PATCH `/api/products/{id}` en el Backend para edición de producto.
- Añadir campo opcional de `description` al dominio `Product` y a `ProductEntity` si no existe.
- Validación de campos obligatorios (Nombre, Precio, Categoría, Estación/Tipo).
- Inactivar un producto (Status = INACTIVE) sin ocultarlo de órdenes ya en curso (`CREATED` o `IN_PROGRESS`).
- Adaptar Frontend para incluir una vista o modal de edición desde la vista `AdminView` de catálogo.
- Manejo de errores de validación (400) y de sistema (500) en el Frontend manteniendo el estado del formulario.

### Out of Scope
- Eliminación física (Hard Delete) de productos en la base de datos (se usa inactivación lógica).
- Modificación de productos ya asociados a un `ProductEntity` o `TaskProductEntity` histórico de un Order (esto ya está resuelto usando la tabla intermedia y nombres inmutables para el historial).

## Approach
Añadir el caso de uso `UpdateProductUseCase` y su puerto correspondiente en `application/`. Se implementará carga de datos actuales en en frontend y su envío a un nuevo endpoint `PUT /api/products/{uuid}` en `ProductController`. La entidad backend y del DTO será actualizada para incluir la `description` (opcional). El `ProductType` servirá como la "Estación". El manejo de órdenes antiguas se mantiene seguro por el diseño ya implementado previamente con la tabla `order_products` que toma un snapshot histórico.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `com.foodtech.kitchen.domain.model.Product` | Modified | Añadir campo `description` y método de actualización `update(...)` |
| `com.foodtech.kitchen.infrastructure.persistence.jpa.entities.ProductEntity` | Modified | Añadir columna `description` |
| `com.foodtech.kitchen.application.usecases.UpdateProductUseCase` | New | Caso de uso para validación y persistencia de cambios |
| `com.foodtech.kitchen.infrastructure.rest.ProductController` | Modified | Endpoint `PUT /api/products/{id}` |
| `FoodTech-Front/src/services/productService.ts` | Modified | Añadir llamada `put` para actualización |
| `FoodTech-Front/src/components/admin/` | New/Mod | Crear vista/modal `EditProductForm.tsx` y listar productos en `AdminView.tsx` |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Órdenes previas afectadas por cambio de precio o estado | Low | La arquitectura ya posee una tabla `order_products` que congela precio, nombre y tipo del momento del pedido. Evitar propagar cambios a esta tabla. |
| Inactivación rompe carga de historial general | Low | El frontend debe estar preparado para mostrar el estado correcto en órdenes sin necesitar buscar `ACTIVE` siempre. |
| Excepciones en guardado parciales | Low | Usar las transacciones Spring (`@Transactional` eventual) e implementación de Rollback. |

## Rollback Plan
1. Revertir el campo `description` en `ProductEntity` si es necesario (migration downgrade manual si aplica).
2. Si existen issues graves, deshabilitar el botón de edición en el Frontend y retornar un 501 / "Feature Disabled" en el controlador de Backend. Revertir a la rama main estable del release anterior.

## Dependencies
- Backend (FoodTech-Kitchen-Services): Spring web y el esquema de base de datos H2/PostgreSQL.
- Frontend (FoodTech-Front): React Router para navegar a `/admin/edit/:id` o state para modal.

## Success Criteria

- [ ] (CA1) Frontend carga todos los campos incluyendo `description` al entrar en edición.
- [ ] (CA2) Se actualiza el producto en Backend y se reflejan los cambios en el catálogo.
- [ ] (CA3) Intentar guardar sin campos requeridos falla con error claro, exceptuando `description`.
- [ ] (CA4) Estado `INACTIVE` hace que el producto no salga en nuevas órdenes pero mantenga integridad de antiguas.
- [ ] (CA5) Caídas de red o servidor no borran el formulario, emitiendo una alerta.
