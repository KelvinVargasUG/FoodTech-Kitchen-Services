# AI WORKFLOW — JWT Authentication Feature

## 1. Contexto del Feature
Este feature implementa autenticacion JWT para registro e inicio de sesion, generacion de token y proteccion de endpoints bajo /api/**. La capa web expone endpoints de auth y delega a casos de uso de aplicacion, mientras la seguridad se aplica con un filtro JWT y una configuracion stateless. Evidencias principales: [src/main/java/com/foodtech/kitchen/infrastructure/rest/AuthController.java](src/main/java/com/foodtech/kitchen/infrastructure/rest/AuthController.java), [src/main/java/com/foodtech/kitchen/application/usecases/RegisterUserUseCase.java](src/main/java/com/foodtech/kitchen/application/usecases/RegisterUserUseCase.java), [src/main/java/com/foodtech/kitchen/application/usecases/AuthenticateUserUseCase.java](src/main/java/com/foodtech/kitchen/application/usecases/AuthenticateUserUseCase.java), [src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java](src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java).

## 2. Estrategia TDD Aplicada
- Casos RED implementados: los commits del branch feature/jwt-auth muestran pruebas RED para endpoints protegidos, JWT expirado, token malformado y firma invalida (commits 7465845, 8fc7766, c405b91), y pruebas RED para registro/login y validaciones (commits 7d1f7b8, 3b20754, 3a4b05e, d61168b, 77a07ae, 208fe85, e430ebc, e8c7241).
- Paso a GREEN: los commits GREEN implementan validaciones, registro, autenticacion y emision de token, y endurecen validaciones de request (commits c5ff902, e8c7241, 8a59a54, d4f7bf1, 127c627).
- Refactor aplicado: sustitucion del stub de TokenGenerator por JwtTokenGenerator y wiring de propiedades (commit d4f7bf1) y ajustes de CI/coverage para asegurar verificacion automatica.
- Decisiones arquitectonicas: validaciones de dominio en casos de uso, Bean Validation en DTOs, y seguridad JWT en filtro antes del UsernamePasswordAuthenticationFilter.

## 3. Arquitectura
- Hexagonal aplicada: casos de uso en application, puertos de salida para dependencias externas y adaptadores de infraestructura.
- Puertos y adaptadores: `TokenGenerator`, `UserRepository` y `PasswordHasher` como puertos ([src/main/java/com/foodtech/kitchen/application/ports/out/TokenGenerator.java](src/main/java/com/foodtech/kitchen/application/ports/out/TokenGenerator.java), [src/main/java/com/foodtech/kitchen/application/ports/out/UserRepository.java](src/main/java/com/foodtech/kitchen/application/ports/out/UserRepository.java), [src/main/java/com/foodtech/kitchen/application/ports/out/PasswordHasher.java](src/main/java/com/foodtech/kitchen/application/ports/out/PasswordHasher.java)); adaptadores concretos en seguridad y persistencia ([src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGenerator.java](src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGenerator.java), [src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters/UserRepositoryAdapter.java](src/main/java/com/foodtech/kitchen/infrastructure/persistence/adapters/UserRepositoryAdapter.java)).
- Separacion application / infrastructure: wiring de beans y dependencias en [src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java](src/main/java/com/foodtech/kitchen/infrastructure/config/ApplicationConfig.java).
- Seguridad y filtro JWT: filtro dedicado en [src/main/java/com/foodtech/kitchen/infrastructure/security/JwtAuthenticationFilter.java](src/main/java/com/foodtech/kitchen/infrastructure/security/JwtAuthenticationFilter.java) y configuracion stateless en [src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java](src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java).

## 4. Testing Strategy Ejecutada
- Unit tests:
  - Casos de uso de autenticacion y registro con mocks ([src/test/java/com/foodtech/kitchen/application/usecases/AuthenticateUserUseCaseTest.java](src/test/java/com/foodtech/kitchen/application/usecases/AuthenticateUserUseCaseTest.java), [src/test/java/com/foodtech/kitchen/application/usecases/RegisterUserUseCaseTest.java](src/test/java/com/foodtech/kitchen/application/usecases/RegisterUserUseCaseTest.java)).
  - Generacion de JWT deterministica y validaciones de constructor ([src/test/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGeneratorTest.java](src/test/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGeneratorTest.java)).
- Integration tests con H2 y Spring Boot:
  - Auth endpoints y validaciones de request ([src/test/java/com/foodtech/kitchen/infrastructure/rest/AuthControllerIntegrationTest.java](src/test/java/com/foodtech/kitchen/infrastructure/rest/AuthControllerIntegrationTest.java)).
  - Seguridad de endpoints protegidos, tokens expirados/malformados/firma invalida ([src/test/java/com/foodtech/kitchen/infrastructure/rest/SecurityIntegrationTest.java](src/test/java/com/foodtech/kitchen/infrastructure/rest/SecurityIntegrationTest.java)).
  - Endpoints protegidos consumidos con Authorization header en flujos existentes ([src/test/java/com/foodtech/kitchen/infrastructure/rest/TaskControllerIntegrationTest.java](src/test/java/com/foodtech/kitchen/infrastructure/rest/TaskControllerIntegrationTest.java)).
- H2 en perfil de test: configuracion en [src/test/resources/application-test.yaml](src/test/resources/application-test.yaml).
- Validacion estructural con Bean Validation en DTO de registro y uso de `@Valid` ([src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/RegisterRequest.java](src/main/java/com/foodtech/kitchen/infrastructure/rest/dto/RegisterRequest.java), [src/main/java/com/foodtech/kitchen/infrastructure/rest/AuthController.java](src/main/java/com/foodtech/kitchen/infrastructure/rest/AuthController.java)).

## 5. Seguridad
- Generacion JWT con HS256 y expiracion basada en `Clock` ([src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGenerator.java](src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenGenerator.java)).
- Validacion JWT y manejo de errores con `JwtTokenValidator` ([src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenValidator.java](src/main/java/com/foodtech/kitchen/infrastructure/security/JwtTokenValidator.java)).
- Manejo de expiracion, token malformado e invalid signature validado en pruebas de integracion ([src/test/java/com/foodtech/kitchen/infrastructure/rest/SecurityIntegrationTest.java](src/test/java/com/foodtech/kitchen/infrastructure/rest/SecurityIntegrationTest.java)).
- Proteccion real de endpoints /api/** en configuracion de seguridad ([src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java](src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java)).

## 6. CI/CD
- Jacoco configurado con verificacion de cobertura y threshold minimo de 0.75 en [build.gradle](build.gradle).
- Pipeline ejecuta `./gradlew clean build --no-daemon` y sube artifact HTML de Jacoco en [ .github/workflows/ci.yml ](.github/workflows/ci.yml).
- El build falla si la cobertura baja bajo el threshold debido a `check.dependsOn jacocoTestCoverageVerification` en [build.gradle](build.gradle).

## 7. Metricas de Cobertura
- Instruction coverage: no disponible en el workspace actual (no se encontro reportes en build/reports/jacoco/test).
- Branch coverage: no disponible en el workspace actual.
- Paquetes con menor cobertura: no disponible sin el reporte Jacoco.
- Nota: ejecutar `./gradlew clean test jacocoTestReport` para generar el XML/HTML y completar esta seccion.

## 8. Deuda Tecnica Identificada
- CSRF esta deshabilitado para desarrollo en la configuracion de seguridad; requiere hardening para produccion ([src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java](src/main/java/com/foodtech/kitchen/infrastructure/config/SecurityConfig.java)).
- No se observa autorizacion por roles/permisos en la configuracion actual (solo autenticacion).
- No hay evidencia de refresh tokens o revocacion/blacklist en el codigo revisado.

## 9. Conclusion Tecnica
El feature JWT esta implementado con separacion hexagonal, seguridad stateless y validaciones tanto en DTOs como en casos de uso. La estrategia TDD queda reflejada en commits RED/GREEN y en el conjunto de pruebas unitarias e integracion ejecutables con H2. El pipeline CI aplica verificacion de cobertura con Jacoco y falla si el threshold no se cumple. Las metricas de cobertura requieren generar el reporte para documentarse en detalle.