# Delta for API and Domain (Kitchen Services)

## ADDED Requirements

### Requirement: Change Product Status

The system MUST allow an authorized user (Admin) to change the status of a product to `INACTIVE` or `ACTIVE`.

#### Scenario: Deactivate an active product successfully

- GIVEN a product exists in the catalog with status `ACTIVE`
- WHEN the administrator sends a request to deactivate the product
- THEN the system MUST update the product's status to `INACTIVE`
- AND the system MUST return a success response.

#### Scenario: Activate an inactive product successfully

- GIVEN a product exists in the catalog with status `INACTIVE`
- WHEN the administrator sends a request to activate the product
- THEN the system MUST update the product's status to `ACTIVE`
- AND the system MUST return a success response.

### Requirement: Filter Active Products

The system MUST return ONLY products with status `ACTIVE` when queried by the Waiter client for order creation.

#### Scenario: Retrieve available products for new order

- GIVEN the catalog contains both `ACTIVE` and `INACTIVE` products
- WHEN the waiter requests the list of products
- THEN the system MUST return only products with status `ACTIVE`
- AND the system MUST NOT return any product with status `INACTIVE`.

## MODIFIED Requirements

### Requirement: Historical Order Integrity

The system MUST preserve product information on historical orders and in-progress orders even if the underlying product is deactivated.
(Previously: The system maintains product information on orders using existing product references)

#### Scenario: View completed order with deactivated product

- GIVEN an order was created containing a product
- AND the product was subsequently changed to status `INACTIVE`
- WHEN a user views the order details (completed or in-progress)
- THEN the system MUST display the product information (name, price) exactly as it was when the order was placed
- AND the order processing MUST NOT be interrupted.