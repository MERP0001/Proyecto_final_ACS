-- ===================================================================
-- MIGRACIÓN V8: Agregar columna fecha_movimiento a movimientos_historial
-- Sistema de Gestión de Inventarios
-- ===================================================================

-- Agregar columna fecha_movimiento a la tabla movimientos_historial
ALTER TABLE movimientos_historial 
ADD COLUMN fecha_movimiento TIMESTAMP WITHOUT TIME ZONE;

-- Agregar comentario explicativo
COMMENT ON COLUMN movimientos_historial.fecha_movimiento IS 'Fecha específica del movimiento (campo adicional para auditoría)';

-- Opcional: Poblar con datos existentes si es necesario
-- UPDATE movimientos_historial SET fecha_movimiento = fecha WHERE fecha_movimiento IS NULL;