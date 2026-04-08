# Product Creation Use Case Specification

## Purpose

Define business logic and validation flow for creating products in the catalog.

## Requirements

### REQ-PC-001: Create Product with Valid Data

The CreateProductUseCase MUST accept valid product data and persist it.

**Input (CreateProductCommand):**
| Field | Validation |
|-------|------------|
| name | Non-empty, max 100 chars |
| price | > 0, max 2 decimal places |
| type | Valid ProductType enum |
| category | Non-empty |

#### Scenario: Successful product creation (CA1)
- GIVEN valid CreateProductCommand
- WHEN execute() is called
- THEN Product is saved to repository
- AND Product ID is returned
- AND Product status is ACTIVE

#### Scenario: Price validation
- GIVEN CreateProductCommand with price = 0
- WHEN execute() is called
- THEN ValidationException is thrown with message "Price must be greater than zero"

### REQ-PC-002: Prevent Duplicate Product Names

The use case MUST reject products with duplicate names (CA3).

#### Scenario: Duplicate name detected
- GIVEN Product "Coca Cola" already exists
- WHEN CreateProductCommand with name "Coca Cola" is submitted
- THEN DuplicateProductException is thrown with message "Product with name 'Coca Cola' already exists"
- AND no Product is created

#### Scenario: Case-sensitive uniqueness
- GIVEN Product "Coca Cola" exists
- WHEN CreateProductCommand with name "coca cola" is submitted
- THEN Product is created successfully
- AND names are treated as case-sensitive

### REQ-PC-003: Mandatory Field Validation

The use case MUST validate all mandatory fields before persistence (CA2).

#### Scenario: Missing category
- GIVEN CreateProductCommand with category = null
- WHEN execute() is called
- THEN ValidationException is thrown
- AND exception message lists "category" as missing field

