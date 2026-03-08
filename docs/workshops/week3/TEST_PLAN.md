# Plan de Pruebas - Feature de Autenticacion

# 1. Introduccion
Este plan define como se verifican las capacidades del feature de autenticacion JWT para el taller de Semana 3. Se basa en los escenarios definidos en [docs/workshops/week2/TESTING_STRATEGY.md](../week2/TESTING_STRATEGY.md) y en los requerimientos derivados en [docs/workshops/week3/REQUIREMENTS_DERIVED_FROM_STRATEGY.md](REQUIREMENTS_DERIVED_FROM_STRATEGY.md). El objetivo es garantizar pruebas repetibles, alineadas con CI, para registro, login, manejo de tokens y acceso a endpoints protegidos.

# 2. Alcance de las Pruebas
| Alcance | Descripcion |
| --- | --- |
| Registro de usuarios | Validacion de email, username y password |
| Login | Autenticacion usando username o email |
| Generacion de JWT | Creacion de token tras autenticacion exitosa |
| Validacion de token | Verificacion de expiracion y formato |
| Acceso a endpoints protegidos | Validacion de autorizacion basada en token |

| Fuera de alcance |
| --- |
| Autorizacion por roles |
| Refresh tokens |
| MFA |
| OAuth |
| Rate limiting |
| Auditoria de seguridad |

# 3. Estrategia de Pruebas Multinivel
| Nivel de prueba | Objetivo | Herramientas | Tipo |
| --- | --- | --- | --- |
| Component Tests | Validar logica interna del dominio | JUnit + Mockito | White Box |
| Integration Tests | Validar comportamiento del sistema completo | SpringBootTest | Black Box |

Las pruebas de caja negra son el enfoque principal para validar el comportamiento observable del sistema. Estas pruebas verifican endpoints REST, respuestas HTTP, acceso a recursos protegidos y el flujo completo de autenticacion sin depender de la implementacion interna. Este enfoque es el que se expone en la defensa del proyecto.

# 4. Tecnicas de Prueba
| Tecnica | Aplicacion |
| --- | --- |
| White Box Testing | Validacion de reglas internas del dominio |
| Black Box Testing | Validacion de endpoints y comportamiento observable |

Las pruebas de caja negra se aplican principalmente en los Integration Tests, donde se valida el comportamiento del sistema desde la perspectiva del cliente.

# 5. Aplicacion de los 7 Principios de Testing
| Principio | Aplicacion en el proyecto |
| --- | --- |
| Testing shows presence of defects | pruebas negativas de validacion |
| Exhaustive testing is impossible | seleccion de escenarios representativos |
| Early testing | ejecucion temprana en CI |
| Defect clustering | foco en login y validaciones |
| Pesticide paradox | ejecucion continua en CI |
| Testing depends on context | uso de H2 en pruebas |
| Absence-of-errors fallacy | passing tests no implica seguridad total |

# 6. Matriz de Trazabilidad
| Requerimiento | Criterio de aceptacion | Nivel de prueba | Tipo de prueba | Metodo de verificacion |
| --- | --- | --- | --- | --- |
| [REQ-AUTH-001](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-001) | AC-AUTH-REG-001 | Component | White Box | Prueba de componente del caso de uso de registro |
| [REQ-AUTH-001](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-001) | AC-AUTH-REG-001 | Integration | Black Box | Flujo del endpoint de registro con SpringBootTest |
| [REQ-AUTH-002](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-002) | AC-AUTH-REG-002 | Component | White Box | Validacion de email duplicado en caso de uso |
| [REQ-AUTH-002](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-002) | AC-AUTH-REG-002 | Integration | Black Box | Endpoint de registro con email existente |
| [REQ-AUTH-003](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-003) | AC-AUTH-REG-003 | Component | White Box | Validacion de username duplicado en caso de uso |
| [REQ-AUTH-003](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-003) | AC-AUTH-REG-003 | Integration | Black Box | Endpoint de registro con username existente |
| [REQ-AUTH-004](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-004) | AC-AUTH-REG-004 | Component | White Box | Regla de validacion de formato de email |
| [REQ-AUTH-005](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-005) | AC-AUTH-REG-005 | Component | White Box | Regla de robustez de password |
| [REQ-AUTH-006](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-006) | AC-AUTH-LOGIN-001 | Component | White Box | Caso de uso de login con username |
| [REQ-AUTH-006](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-006) | AC-AUTH-LOGIN-001 | Integration | Black Box | Endpoint de login con username |
| [REQ-AUTH-007](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-007) | AC-AUTH-LOGIN-002 | Component | White Box | Caso de uso de login con email |
| [REQ-AUTH-007](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-007) | AC-AUTH-LOGIN-002 | Integration | Black Box | Endpoint de login con email |
| [REQ-AUTH-008](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-008) | AC-AUTH-LOGIN-003 | Component | White Box | Respuesta de password invalido en caso de uso |
| [REQ-AUTH-009](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-009) | AC-AUTH-LOGIN-004 | Component | White Box | Rechazo de usuario inactivo |
| [REQ-AUTH-010](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-010) | AC-AUTH-TOKEN-001 | Component | White Box | Generacion y validacion de token |
| [REQ-AUTH-011](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-011) | AC-AUTH-TOKEN-002 | Component | White Box | Validacion de expiracion de token |
| [REQ-AUTH-012](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-012) | AC-AUTH-TOKEN-003 | Component | White Box | Validacion de token malformado |
| [REQ-AUTH-013](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-013) | AC-AUTH-PROTECT-001 | Integration | Black Box | Endpoint protegido sin token |
| [REQ-AUTH-014](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-014) | AC-AUTH-PROTECT-002 | Integration | Black Box | Endpoint protegido con token valido |

# 7. Casos de Prueba
| ID | Requerimiento | Criterio | Nivel | Tipo | Precondicion | Resultado esperado |
| --- | --- | --- | --- | --- | --- | --- |
| TC-AUTH-REG-001 | [REQ-AUTH-001](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-001) | AC-AUTH-REG-001 | Integration | Black Box | Usuario no existe en H2 | Registro exitoso |
| TC-AUTH-REG-002 | [REQ-AUTH-002](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-002) | AC-AUTH-REG-002 | Integration | Black Box | Email ya registrado | Registro rechazado por email duplicado |
| TC-AUTH-REG-003 | [REQ-AUTH-003](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-003) | AC-AUTH-REG-003 | Integration | Black Box | Username ya registrado | Registro rechazado por username duplicado |
| TC-AUTH-REG-004 | [REQ-AUTH-004](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-004) | AC-AUTH-REG-004 | Component | White Box | Ninguna | Registro rechazado por email invalido |
| TC-AUTH-REG-005 | [REQ-AUTH-005](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-005) | AC-AUTH-REG-005 | Component | White Box | Ninguna | Registro rechazado por password debil |
| TC-AUTH-LOGIN-001 | [REQ-AUTH-006](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-006) | AC-AUTH-LOGIN-001 | Integration | Black Box | Usuario activo con username | Login exitoso y token JWT devuelto |
| TC-AUTH-LOGIN-002 | [REQ-AUTH-007](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-007) | AC-AUTH-LOGIN-002 | Integration | Black Box | Usuario activo con email | Login exitoso y token JWT devuelto |
| TC-AUTH-LOGIN-003 | [REQ-AUTH-008](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-008) | AC-AUTH-LOGIN-003 | Component | White Box | Usuario activo existente | Login rechazado por password invalido |
| TC-AUTH-LOGIN-004 | [REQ-AUTH-009](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-009) | AC-AUTH-LOGIN-004 | Component | White Box | Usuario inactivo existente | Login rechazado por usuario inactivo |
| TC-AUTH-TOKEN-001 | [REQ-AUTH-010](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-010) | AC-AUTH-TOKEN-001 | Component | White Box | Autenticacion exitosa | Token generado y validado |
| TC-AUTH-TOKEN-002 | [REQ-AUTH-011](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-011) | AC-AUTH-TOKEN-002 | Component | White Box | Token expirado | Validacion falla por expiracion |
| TC-AUTH-TOKEN-003 | [REQ-AUTH-012](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-012) | AC-AUTH-TOKEN-003 | Component | White Box | Token malformado | Validacion falla por formato |
| TC-AUTH-PROTECT-001 | [REQ-AUTH-013](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-013) | AC-AUTH-PROTECT-001 | Integration | Black Box | Endpoint protegido disponible | Acceso denegado sin token |
| TC-AUTH-PROTECT-002 | [REQ-AUTH-014](./REQUIREMENTS_DERIVED_FROM_STRATEGY.md#req-auth-014) | AC-AUTH-PROTECT-002 | Integration | Black Box | Token valido obtenido por login | Acceso permitido con token valido |

# 8. Integracion con CI/CD
La canalizacion de CI descrita en [CI workflow](.github/workflows/ci.yml) ejecuta pruebas unitarias, de componente e integracion, y valida seguridad y empaquetado. Esto asegura que el feature se verifique en cada cambio.

| Job del pipeline | Proposito |
| --- | --- |
| unit-tests | validar logica basica |
| component-tests | validar reglas de negocio |
| integration-tests | validar comportamiento del sistema |
| docker-build | construir imagen |
| trivy-scan | escaneo de vulnerabilidades |
| sbom | generacion de SBOM |

# 9. Evidencia de Ejecucion
## 9.1 Pipeline CI exitoso
Captura esperada: [docs/workshops/week3/evidence/pipeline-success.png](evidence/pipeline-success.png)
Descripcion: Captura del pipeline en estado GREEN donde todos los jobs finalizan correctamente.

## 9.2 Ejecucion de pruebas automatizadas
Captura esperada: [docs/workshops/week3/evidence/tests-execution.png](evidence/tests-execution.png)
Descripcion: Captura del job donde se observa la ejecucion de unit tests, component tests e integration tests.

## 9.3 Escaneo de seguridad de imagen Docker
Captura esperada: [docs/workshops/week3/evidence/trivy-report.png](evidence/trivy-report.png)
Descripcion: Resultado del escaneo de vulnerabilidades generado por Trivy.

## 9.4 Generacion de SBOM
Captura esperada: [docs/workshops/week3/evidence/sbom-artifact.png](evidence/sbom-artifact.png)
Descripcion: Artifact generado con CycloneDX mostrando la lista de dependencias del proyecto.
