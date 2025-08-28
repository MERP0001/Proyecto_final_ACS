# language: es
@autenticacion
Característica: Sistema de autenticación y autorización
  Como usuario del sistema
  Quiero autenticarme de forma segura
  Para acceder a las funcionalidades según mi rol

  @login-exitoso
  Escenario: Login exitoso con credenciales válidas
    Dado que tengo credenciales válidas:
      | email    | admin@example.com |
      | password | password          |
    Cuando envío una solicitud de login
    Entonces debo recibir un token JWT válido
    Y el token debe contener la información del usuario
    Y el token debe tener una fecha de expiración válida

  @login-fallido
  Esquema del escenario: Login fallido con credenciales inválidas
    Dado que tengo las siguientes credenciales:
      | email   | password   |
      | <email> | <password> |
    Cuando envío una solicitud de login
    Entonces debo recibir un error de autenticación
    Y no debo recibir un token

    Ejemplos:
      | email              | password    |
      | admin@example.com  | wrongpass   |
      | wrong@example.com  | password    |
      | ""                 | password    |
      | admin@example.com  | ""          |

  @acceso-sin-token
  Escenario: Acceso a recurso protegido sin token
    Cuando intento acceder a un recurso protegido sin token
    Entonces debo recibir un error de autorización 401
    Y no debo poder acceder al recurso

  @acceso-token-expirado
  Escenario: Acceso con token expirado
    Dado que tengo un token JWT expirado
    Cuando intento acceder a un recurso protegido
    Entonces debo recibir un error de autorización 401
    Y debo ser redirigido al login

  @refresh-token
  Escenario: Renovación de token válido
    Dado que tengo un token JWT válido próximo a expirar
    Cuando solicito renovar el token
    Entonces debo recibir un nuevo token válido
    Y el nuevo token debe tener una nueva fecha de expiración
