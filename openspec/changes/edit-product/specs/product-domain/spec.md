# Product Domain Specification

## Purpose

Define the business rules, properties, and valid states for a catalog product within the FoodTech Kitchen Services domain.

## Requirements

### Requirement: Product Core Data

The system MUST store the core data of a product: `id`, `name`, `type` (ProductType), `category`, `price`, and `status`. The system SHALL additionally store an optional `description`.

#### Scenario: Successful Product Creation or Reconstruction with Description
- GIVEN valid inputs including an optional description
- WHEN a `Product` is created or reconstructed
- THEN the product instance MUST hold the `description` value
- AND the value MAY be null

### Requirement: Product Modification

The system MUST allow modifying the editable fields of an existing product (name, type, category, price, status, and description) without altering its unique `id`.

#### Scenario: Valid Modification
- GIVEN an existing `Product` instance
- WHEN its editable fields are updated with valid data
- THEN the product MUST reflect the new data
- AND the `id` MUST remain unchanged

### Requirement: Update Validation

The system MUST enforce business rules during modification: name MUST NOT be blank, type MUST NOT be null, category MUST NOT be blank, and price MUST be greater than zero. Description MAY be null or blank. Status MUST NOT be null.

#### Scenario: Invalid Price Update
- GIVEN an existing `Product` instance
- WHEN an update is attempted with a price of zero or less
- THEN the system MUST throw an `IllegalArgumentException`
- AND the product MUST NOT be modified

#### Scenario: Optional Description
- GIVEN an existing `Product` instance
- WHEN an update is attempted where `description` is empty or null
- THEN the system MUST accept and apply the update
