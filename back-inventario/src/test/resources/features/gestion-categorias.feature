# language: es
@categorias
Característica: Gestión de categorías de productos
  Como administrador del sistema
  Quiero gestionar las categorías de productos
  Para organizar mejor el inventario

  Antecedentes:
    Dado que soy un administrador autenticado
    Y el sistema está conectado a la base de datos

  @crear-categoria
  Escenario: Crear una nueva categoría
    Cuando creo una categoría con los siguientes datos:
      | nombre      | Laptops Gaming              |
      | descripcion | Laptops especializadas para gaming |
    Entonces la categoría debe ser creada exitosamente
    Y debe tener un ID asignado
    Y los datos de la categoría deben coincidir con los ingresados

  @listar-categorias
  Escenario: Listar todas las categorías
    Dado que existen categorías en el sistema
    Cuando solicito la lista de categorías
    Entonces debo obtener una lista de categorías
    Y cada categoría debe tener nombre y descripción

  @actualizar-categoria
  Escenario: Actualizar una categoría existente
    Dado que existe una categoría con nombre "Laptops Gaming"
    Cuando actualizo la descripción a "Laptops de alta gama para gaming"
    Entonces la categoría debe ser actualizada exitosamente
    Y la nueva descripción debe coincidir

  @eliminar-categoria
  Escenario: Eliminar una categoría sin productos asociados
    Dado que existe una categoría sin productos asociados
    Cuando elimino la categoría
    Entonces la categoría debe ser eliminada exitosamente
    Y no debe aparecer en la lista de categorías

  @eliminar-categoria-con-productos
  Escenario: Intentar eliminar categoría con productos
    Dado que existe una categoría con productos asociados
    Cuando intento eliminar la categoría
    Entonces debe mostrar un error de referencia
    Y la categoría debe permanecer en el sistema
