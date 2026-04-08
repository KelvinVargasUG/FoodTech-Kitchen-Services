# Product API Specification

## Purpose

Define REST endpoint contract for product creation.

## Requirements

### REQ-API-001: Create Product Endpoint

The API MUST expose POST /api/products for product creation.

**Request:**
```json
POST /api/products
{
  "name": "string",
  "price": number,
  "type": "BEER|FOOD|DESSERT|...",
  "category": "string"
}
```

**Response (201 Created):**
```json
{
  "id": number,
  "name": "string",
  "price": number,
  "type": "string",
  "status": "ACTIVE",
  "category": "string"
}
```

#### Scenario: Successful product creation via API (CA1)
- GIVEN valid JSON request body
- WHEN POST /api/products is called
- THEN response status is 201 Created
- AND Location header contains /api/products/{id}
- AND response body contains created Product with ID
- AND status field is "ACTIVE"

#### Scenario: Validation error response (CA2)
- GIVEN request with missing "name" field
- WHEN POST /api/products is called
- THEN response status is 400 Bad Request
- AND response body contains validation error
- AND error message states "name is required"

#### Scenario: Duplicate name error response (CA3)
- GIVEN Product "Sprite" already exists
- WHEN POST /api/products with name "Sprite" is called
- THEN response status is 409 Conflict
- AND response body contains error message "Product with name 'Sprite' already exists"

### REQ-API-002: Error Response Format

API error responses MUST follow standard format.

**Error Response:**
```json
{
  "timestamp": "ISO-8601",
  "status": number,
  "error": "string",
  "message": "string",
  "path": "/api/products"
}
```

#### Scenario: Standard error format
- GIVEN any validation error
- WHEN error response is returned
- THEN response MUST include timestamp, status, error, message, path fields

