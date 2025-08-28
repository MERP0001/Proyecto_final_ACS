# language: es
@database
Característica: Prueba de integración con base de datos
  Como desarrollador
  Quiero verificar que la integración con la base de datos funciona
  Para asegurar que el sistema persiste datos correctamente

  @integration
  Escenario: Crear categoría en la base de datos
    Dado que la base de datos está disponible
    Cuando creo una categoria de prueba
    Entonces la categoria debe guardarse en la base de datos