# language: es
@regresion @critico
Característica: Suite de Regresión - Funcionalidades Críticas
  Como desarrollador del sistema
  Quiero ejecutar pruebas de regresión automáticas
  Para asegurar que los cambios no afecten funcionalidades existentes

  # Pruebas críticas que SIEMPRE deben pasar
  @regresion-auth
  Escenario: Regresión - Autenticación básica funciona
    Dado que tengo credenciales válidas:
      | email    | admin@example.com |
      | password | password          |
    Cuando envío una solicitud de login
    Entonces debo recibir un token JWT válido
    Y el token debe contener la información del usuario

  @regresion-productos
  Escenario: Regresión - CRUD básico de productos
    Dado que soy un administrador autenticado
    Y el sistema está conectado a la base de datos
    Cuando creo un producto con los siguientes datos:
      | nombre      | Producto Regresión Test |
      | descripcion | Producto para regresión |
      | categoria   | Testing                 |
      | precio      | 99.99                   |
      | cantidad    | 50                      |
      | sku         | REG-TEST-001            |
    Entonces el producto debe ser creado exitosamente
    Y debe tener un ID asignado
    
  @regresion-busqueda
  Escenario: Regresión - Búsqueda de productos funciona
    Dado que existen productos en el sistema
    Cuando busco productos con nombre "Test"
    Entonces debo obtener una lista de productos

  @regresion-categorias
  Escenario: Regresión - Listar categorías funciona
    Dado que existen categorías en el sistema
    Cuando solicito la lista de categorías
    Entonces debo obtener una lista de categorías
    Y cada categoría debe tener nombre y descripción

  @regresion-api
  Escenario: Regresión - Endpoints principales responden
    Dado que soy un administrador autenticado
    Cuando solicito la lista de productos
    Entonces debo obtener una respuesta del sistema

  @regresion-seguridad
  Escenario: Regresión - Seguridad básica funciona
    Cuando intento acceder a un recurso protegido sin token
    Entonces debo recibir un error de autorización 401
    Y no debo poder acceder al recurso
