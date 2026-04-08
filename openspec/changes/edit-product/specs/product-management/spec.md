# Product Management API Specification

## Purpose

Define the application and infrastructure layer boundaries for the `/api/products` management capabilities, specifically updating existing resources.

## Requirements

### Requirement: Update Resource Metadata

The system MUST expose a `PUT /api/products/{id}` REST endpoint representing current catalog state, accessible for administrators to update product fields. The `id` MUST be a structurally valid UUID matching an existing product record.

#### Scenario: Successful Update
- GIVEN a valid `id` exists in the database
- WHEN an administrator sends a valid JSON payload to `PUT /api/products/{id}` containing: name, optional description, type, category, price, and status
- THEN the system MUST return `200 OK`
- AND the response body MUST contain the updated product payload matching `CreateProductResponse` format.
- AND the database record MUST be immediately modified.

#### Scenario: Not Found Error
- GIVEN an `id` that does NOT exist in the database
- WHEN a `PUT` request is submitted with a valid payload
- THEN the system MUST return a `404 Not Found` response.

#### Scenario: Payload Validation Failure
- GIVEN an `id` matching an existing record
- WHEN a `PUT` payload omits a required field (e.g. `price`) or passes an invalid `type` enum
- THEN the system MUST return a `400 Bad Request`
- AND the original product MUST NOT be modified in the database.

### Requirement: Update Business Validation Execution

The system MUST ensure that `UpdateProductUseCase` applies domain validation, retrieves the existing product domain object by ID, delegates the update via domain object update mechanics, and persists back through `ProductRepository`. 

#### Scenario: Existing Product Change Status to INACTIVE
- GIVEN an existing product with `Status = ACTIVE`
- WHEN an administrator initiates updates changing only `status` to `INACTIVE`
- THEN the system MUST persist the product with the updated `INACTIVE` status
- AND `UpdateProductUseCase` MUST map it back and report success.
