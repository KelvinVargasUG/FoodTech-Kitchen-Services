# Product Domain Specification

## Purpose

Define the Product aggregate root with identity, lifecycle, and business constraints for catalog management.

## Requirements

### REQ-PD-001: Product Identity

The Product entity MUST be an identifiable aggregate root with a unique ID.

**Properties:**
| Field | Type | Constraint | Default |
|-------|------|------------|---------|
| id | Long | Auto-generated, non-null | N/A |
| name | String | Non-empty, unique | N/A |
| price | BigDecimal | > 0 | N/A |
| type | ProductType | Non-null, valid enum | N/A |
| status | ProductStatus | Non-null | ACTIVE |
| category | String | Non-null | N/A |

#### Scenario: Create new product with valid data
- GIVEN all mandatory fields provided (name, price, type, category)
- WHEN Product entity is created
- THEN Product has auto-generated ID
- AND status defaults to ACTIVE
- AND all fields are persisted

#### Scenario: Create product with missing mandatory field
- GIVEN name is null or empty
- WHEN Product creation is attempted
- THEN validation exception is thrown
- AND Product is not created

### REQ-PD-002: Product Status Lifecycle

The Product MUST support status transitions between ACTIVE and INACTIVE.

#### Scenario: Product created as active
- GIVEN new Product is created
- WHEN no status is specified
- THEN status MUST default to ACTIVE

### REQ-PD-003: Station Assignment

Each Product MUST be assigned to a kitchen station via ProductType.

#### Scenario: Determine station from product type
- GIVEN Product with type = BEER
- WHEN station is queried
- THEN station MUST be BAR
- AND station is derived from ProductType.getStation()

