# Product UI Specification

## Purpose

Define the user interface interactions and behaviors for the FoodTech-Front React application governing product editing in the `/admin` dashboard.

## Requirements

### Requirement: AdminView Edit Action

The system MUST provide an action on each catalog item in `AdminView.tsx` to initiate the editing sequence, which MUST open an editing form pre-loaded with current product data.

#### Scenario: Open Edit Form
- GIVEN a table of products in the admin view
- WHEN an administrator clicks on the Edit icon/action for a specific product "Lomo Saltado"
- THEN an edit form or modal MUST appear
- AND it MUST pre-populate inputs for `name`, `type` (station), `category`, `price`, and `status`. It MAY include `description`.

### Requirement: Update Submission

The system MUST submit modified fields to the backend via `productService.put(...)`, reflecting the payload in the server catalog and reloading/reflecting the catalog on successful `200 OK`.

#### Scenario: Successful Update
- GIVEN the edit form is loaded for a product
- WHEN an administrator edits the price from 1200 to 1400 and clicks Save
- THEN the system MUST send a `PUT` request to `/api/products/{id}`
- AND the system MUST display a success confirmation message upon the successful response
- AND the UI MUST update to reflect the new price.

#### Scenario: Field Validation Enforcement
- GIVEN an active edit form for a product
- WHEN the administrator clears the `name` field and attempts to Save
- THEN the system MUST NOT send a network request
- AND it MUST display validation errors indicating the `name` field is required.

#### Scenario: Inactivate Product
- GIVEN the edit form for a product with `Status = ACTIVE`
- WHEN an administrator selects the `INACTIVE` state mapping and clicks Save
- THEN the product MUST be submitted with `status: INACTIVE`
- AND upon completion, the product MUST no longer appear as selectable for front-facing waiter creation logic but MUST remain visible in the Admin catalog.

#### Scenario: Network Failure Handling
- GIVEN an active edit form with modified fields
- WHEN a `PUT` submission is attempted during network instability or internal server error (`500`)
- THEN an error alert MUST be displayed: "No se pudieron guardar los cambios. Por favor, intente nuevamente."
- AND the form data MUST remain populated so the user's progress is not lost.