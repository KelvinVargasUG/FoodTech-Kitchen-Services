# Delta for UI (FoodTech-Front)

## ADDED Requirements

### Requirement: Action Buttons for Deactivation/Activation

The UI MUST display a "Deactivate" action for `ACTIVE` products and an "Activate" action for `INACTIVE` products in the Administrator catalog view.

#### Scenario: Display Deactivate action

- GIVEN a product is displayed in the catalog
- AND its status is `ACTIVE`
- WHEN the administrator views the product list
- THEN the system MUST display a "Deactivate" button.
- AND clicking the button MUST prompt for confirmation and change the status.

#### Scenario: Display Activate action

- GIVEN a product is displayed in the catalog
- AND its status is `INACTIVE`
- WHEN the administrator views the product list
- THEN the system MUST display an "Activate" button.
- AND clicking the button MUST prompt for confirmation and change the status.

### Requirement: Filter Active Products in Waiter View

The UI MUST NOT display products with status `INACTIVE` in the Waiter order creation view.

#### Scenario: Waiter views available products

- GIVEN the catalog contains both `ACTIVE` and `INACTIVE` products
- WHEN the waiter views the products available for an order
- THEN the system MUST only display products with status `ACTIVE`.