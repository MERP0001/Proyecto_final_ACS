# language: es
@productos-simple
Característica: Verificación básica del sistema de productos
  Como desarrollador
  Quiero verificar que la configuración de pruebas funciona
  Para poder construir pruebas más complejas

  @conexion
  Escenario: Verificar conexión a la base de datos
    Dado que el sistema está conectado a la base de datos
    Cuando solicito la lista de productos
    Entonces debo obtener una respuesta del sistema 