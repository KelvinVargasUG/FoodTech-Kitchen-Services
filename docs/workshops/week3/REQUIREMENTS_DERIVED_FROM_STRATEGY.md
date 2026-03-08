# Requisitos Derivados para Feature de Autenticacion

## Alcance del Feature
Derivado de [docs/workshops/week2/TESTING_STRATEGY.md](../week2/TESTING_STRATEGY.md). El feature cubre autenticacion JWT con registro de usuarios, login por username o email, emision y validacion de tokens (incluyendo expiracion y tokens malformados), y control de acceso a endpoints protegidos segun la presencia y validez del token. La autorizacion por roles, refresh tokens y hardening adicional quedan fuera de alcance segun la estrategia.

## Requisitos Funcionales
### REQ-AUTH-001
El usuario puede registrarse con email, username y password validos.

### REQ-AUTH-002
El registro rechaza emails duplicados.

### REQ-AUTH-003
El registro rechaza usernames duplicados.

### REQ-AUTH-004
El registro rechaza formatos de email invalidos.

### REQ-AUTH-005
El registro rechaza passwords debiles (menos de 6 caracteres, sin numero o sin letra).

### REQ-AUTH-006
El usuario puede iniciar sesion con username y credenciales validas.

### REQ-AUTH-007
El usuario puede iniciar sesion con email y credenciales validas.

### REQ-AUTH-008
El login rechaza credenciales invalidas.

### REQ-AUTH-009
El login rechaza usuarios inactivos.

### REQ-AUTH-010
Se genera un token JWT despues de una autenticacion exitosa.

### REQ-AUTH-011
La validacion de token detecta expiracion.

### REQ-AUTH-012
La validacion de token rechaza tokens malformados.

### REQ-AUTH-013
Los endpoints protegidos deniegan acceso cuando falta el token.

### REQ-AUTH-014
Los endpoints protegidos permiten acceso cuando el token es valido.

## Criterios de Aceptacion
### AC-AUTH-REG-001
Dado un usuario nuevo con email, username y password validos
Cuando el usuario envia los datos de registro
Entonces la cuenta se crea correctamente.

### AC-AUTH-REG-002
Dado que existe un usuario con el mismo email
Cuando se envia un nuevo registro
Entonces el registro se rechaza por email duplicado.

### AC-AUTH-REG-003
Dado que existe un usuario con el mismo username
Cuando se envia un nuevo registro
Entonces el registro se rechaza por username duplicado.

### AC-AUTH-REG-004
Dado un usuario nuevo con un email de formato invalido
Cuando el usuario envia los datos de registro
Entonces el registro se rechaza por email invalido.

### AC-AUTH-REG-005
Dado un usuario nuevo con un password debil (menos de 6 caracteres, sin numero o sin letra)
Cuando el usuario envia los datos de registro
Entonces el registro se rechaza por password debil.

### AC-AUTH-LOGIN-001
Dado un usuario registrado y activo
Cuando el usuario inicia sesion con username y password validos
Entonces la autenticacion es exitosa y se retorna un token JWT.

### AC-AUTH-LOGIN-002
Dado un usuario registrado y activo
Cuando el usuario inicia sesion con email y password validos
Entonces la autenticacion es exitosa y se retorna un token JWT.

### AC-AUTH-LOGIN-003
Dado un usuario registrado y activo
Cuando el usuario inicia sesion con un password invalido
Entonces la autenticacion es rechazada.

### AC-AUTH-LOGIN-004
Dado un usuario registrado e inactivo
Cuando el usuario intenta iniciar sesion con credenciales validas
Entonces la autenticacion es rechazada.

### AC-AUTH-TOKEN-001
Dado una autenticacion exitosa
Cuando el sistema emite un token
Entonces el JWT se genera y puede validarse.

### AC-AUTH-TOKEN-002
Dado un token expirado
Cuando el token es validado
Entonces la validacion falla por expiracion.

### AC-AUTH-TOKEN-003
Dado un token malformado
Cuando el token es validado
Entonces la validacion falla por formato de token.

### AC-AUTH-PROTECT-001
Dado un endpoint protegido
Cuando se realiza una solicitud sin token
Entonces el acceso es denegado.

### AC-AUTH-PROTECT-002
Dado un endpoint protegido
Cuando se realiza una solicitud con un token valido
Entonces el acceso es permitido.

## Mapeo Refinado a Niveles de Prueba
| Requerimiento | Criterio de aceptacion | Nivel de prueba (Componente / Integracion) | Tecnica de prueba (White Box / Black Box) | Escenario cubierto (Testing Strategy) |
| --- | --- | --- | --- | --- |
| REQ-AUTH-001 | AC-AUTH-REG-001 | Component Test | White Box | Register success |
| REQ-AUTH-001 | AC-AUTH-REG-001 | Integration Test | Black Box | Register success |
| REQ-AUTH-002 | AC-AUTH-REG-002 | Component Test | White Box | Register duplicate email |
| REQ-AUTH-002 | AC-AUTH-REG-002 | Integration Test | Black Box | Register duplicate email |
| REQ-AUTH-003 | AC-AUTH-REG-003 | Component Test | White Box | Register duplicate username |
| REQ-AUTH-003 | AC-AUTH-REG-003 | Integration Test | Black Box | Register duplicate username |
| REQ-AUTH-004 | AC-AUTH-REG-004 | Component Test | White Box | Invalid email format |
| REQ-AUTH-005 | AC-AUTH-REG-005 | Component Test | White Box | Weak password (menos de 6, sin numero, sin letra) |
| REQ-AUTH-006 | AC-AUTH-LOGIN-001 | Component Test | White Box | Login success (username) |
| REQ-AUTH-006 | AC-AUTH-LOGIN-001 | Integration Test | Black Box | Login success (username) |
| REQ-AUTH-007 | AC-AUTH-LOGIN-002 | Component Test | White Box | Login success (email) |
| REQ-AUTH-007 | AC-AUTH-LOGIN-002 | Integration Test | Black Box | Login success (email) |
| REQ-AUTH-008 | AC-AUTH-LOGIN-003 | Component Test | White Box | Login wrong password |
| REQ-AUTH-009 | AC-AUTH-LOGIN-004 | Component Test | White Box | Login inactive user |
| REQ-AUTH-010 | AC-AUTH-TOKEN-001 | Component Test | White Box | Token generation |
| REQ-AUTH-011 | AC-AUTH-TOKEN-002 | Component Test | White Box | Token expiration validation |
| REQ-AUTH-012 | AC-AUTH-TOKEN-003 | Component Test | White Box | Token malformed |
| REQ-AUTH-013 | AC-AUTH-PROTECT-001 | Integration Test | Black Box | Protected endpoint without token |
| REQ-AUTH-014 | AC-AUTH-PROTECT-002 | Integration Test | Black Box | Protected endpoint with valid token |
