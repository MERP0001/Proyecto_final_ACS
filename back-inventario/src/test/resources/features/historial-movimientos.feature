# language: es
@historial-movimientos
Característica: Seguimiento de movimientos de inventario
  Como administrador del sistema
  Quiero rastrear todos los movimientos de stock
  Para mantener un historial completo del inventario

  Antecedentes:
    Dado que soy un administrador autenticado
    Y el sistema está conectado a la base de datos

  @movimiento-entrada
  Escenario: Registrar entrada de stock
    Dado que existe un producto con stock actual de 10 unidades
    Cuando registro una entrada de 5 unidades con motivo "Compra a proveedor"
    Entonces el stock del producto debe aumentar a 15 unidades
    Y debe crearse un registro de movimiento de tipo "ENTRADA"
    Y el historial debe mostrar el movimiento registrado

  @movimiento-salida
  Escenario: Registrar salida de stock
    Dado que existe un producto con stock actual de 15 unidades
    Cuando registro una salida de 3 unidades con motivo "Venta"
    Entonces el stock del producto debe disminuir a 12 unidades
    Y debe crearse un registro de movimiento de tipo "SALIDA"
    Y el historial debe mostrar el movimiento registrado

  @stock-insuficiente
  Escenario: Intentar salida con stock insuficiente
    Dado que existe un producto con stock actual de 5 unidades
    Cuando intento registrar una salida de 10 unidades
    Entonces debe mostrar un error de stock insuficiente
    Y el stock del producto debe permanecer en 5 unidades
    Y no debe crearse ningún registro de movimiento

  @historial-por-producto
  Escenario: Consultar historial de movimientos por producto
    Dado que un producto tiene varios movimientos registrados
    Cuando consulto el historial de movimientos del producto
    Entonces debo obtener una lista ordenada por fecha
    Y cada movimiento debe mostrar fecha, tipo, cantidad y motivo
    Y el balance final debe coincidir con el stock actual

  @reporte-movimientos
  Escenario: Generar reporte de movimientos por período
    Dado que existen movimientos en diferentes fechas
    Cuando solicito un reporte de movimientos entre "2024-01-01" y "2024-12-31"
    Entonces debo obtener todos los movimientos del período
    Y el reporte debe incluir totales por tipo de movimiento
    Y debe mostrar el balance neto del período
