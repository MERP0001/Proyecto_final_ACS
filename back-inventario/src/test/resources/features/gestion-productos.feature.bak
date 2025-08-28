# language: es
@productos
Característica: Gestión completa de productos
  Como administrador del sistema
  Quiero gestionar el inventario de productos
  Para mantener actualizada la información de stock

  Antecedentes:
    Dado que soy un administrador autenticado
    Y el sistema está conectado a la base de datos

  @crear-producto
  Escenario: Crear un nuevo producto completo
    Cuando creo un producto con los siguientes datos:
      | nombre      | Monitor Samsung 32"           |
      | descripcion | Monitor 4K para gaming        |
      | categoria   | Monitores                     |
      | precio      | 499.99                        |
      | cantidad    | 15                            |
      | sku         | MON-SAM32-4K                  |
    Entonces el producto debe ser creado exitosamente
    Y debe tener un ID asignado
    Y los datos del producto deben coincidir con los ingresados

  @buscar-productos
  Escenario: Buscar productos por nombre
    Dado que existen productos en el sistema
    Cuando busco productos con nombre "Monitor"
    Entonces debo obtener una lista de productos
    Y todos los productos deben contener "Monitor" en su nombre

  @actualizar-producto
  Escenario: Actualizar precio de un producto
    Dado que existe un producto con SKU "MON-SAM32-4K"
    Cuando actualizo el precio a 549.99
    Entonces el producto debe ser actualizado exitosamente
    Y el nuevo precio debe ser 549.99

  @eliminar-producto
  Escenario: Eliminación lógica de producto
    Dado que existe un producto con SKU "MON-SAM32-4K"
    Cuando elimino el producto
    Entonces el producto debe ser marcado como inactivo
    Y no debe aparecer en las búsquedas regulares

  @validacion-datos
  Esquema del escenario: Validar datos requeridos al crear producto
    Cuando intento crear un producto con datos:
      | nombre   | descripcion   | precio   | cantidad   |
      | <nombre> | <descripcion> | <precio> | <cantidad> |
    Entonces debería obtener el resultado: <resultado>

    Ejemplos:
      | nombre    | descripcion | precio | cantidad | resultado |
      | ""        | Desc test   | 100.0  | 10       | error     |
      | Producto1 | ""          | 100.0  | 10       | error     |
      | Producto2 | Desc test   | 0      | 10       | error     |
      | Producto3 | Desc test   | 100.0  | -1       | error     |
      | Producto4 | Desc test   | 100.0  | 10       | exito     |
