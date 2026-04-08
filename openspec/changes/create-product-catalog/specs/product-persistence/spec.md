# Product Persistence Specification

## Purpose

Define repository behavior for Product persistence and uniqueness enforcement.

## Requirements

### REQ-PP-001: Product Repository Port

The ProductRepository port MUST provide persistence operations.

**Interface:**
```
Product save(Product product)
boolean existsByName(String name)
```

#### Scenario: Save new product
- GIVEN valid Product entity
- WHEN save() is called
- THEN Product is persisted to database
- AND generated ID is assigned
- AND persisted Product is returned

#### Scenario: Check name uniqueness
- GIVEN Product "Pizza" exists in database
- WHEN existsByName("Pizza") is called
- THEN returns true

### REQ-PP-002: Database Uniqueness Constraint

The ProductEntity table MUST enforce unique constraint on name column.

#### Scenario: Concurrent duplicate creation prevented
- GIVEN two concurrent requests to create "Burger"
- WHEN both transactions attempt to commit
- THEN one MUST succeed
- AND the other MUST fail with DataIntegrityViolationException
- AND database constraint prevents duplicate

### REQ-PP-003: ProductEntity Mapping

The ProductEntity MUST map all Product domain fields.

**Mapping:**
| Domain Field | Entity Column | JPA Annotation |
|--------------|---------------|----------------|
| id | id | @Id, @GeneratedValue |
| name | name | @Column(unique=true, nullable=false) |
| price | price | @Column(nullable=false) |
| type | type | @Enumerated(STRING) |
| status | status | @Enumerated(STRING) |
| category | category | @Column(nullable=false) |

