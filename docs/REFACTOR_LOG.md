# REFACTOR LOG

## PR0 - Baseline Architecture Definition
- Added documentation baseline
- No production code modified
- Build verified with ./gradlew test

## PR1 - Restore Hexagonal Boundary for Task Filtering
- Controller no longer accesses repository directly
- Filtering logic centralized in application use case
- Build verified with ./gradlew test

## PR2 - Strategy Pattern for CommandFactory
- Switch-case removed from CommandFactory
- Strategy Pattern implemented for command selection
- OCP restored and workshop compliance achieved
- Build verified with ./gradlew test

## PR3 - Extract Async Execution to Infrastructure
- Removed Reactor from application layer
- Introduced AsyncCommandDispatcher port
- Moved async callbacks to infrastructure
- SRP and DIP restored
- Build verified

## PR4 - Remove Framework Annotations from Application Layer
- Eliminated Spring annotations from application
- Moved bean wiring to infrastructure config
- Application layer now framework-free
- Clean Architecture boundary enforced
- Build verified

## PR5 - Unit Test (Proof of Decoupling)
- Added pure unit test with mocks
- No framework dependency in application layer
- Verified test runs without Spring context
- Build verified
