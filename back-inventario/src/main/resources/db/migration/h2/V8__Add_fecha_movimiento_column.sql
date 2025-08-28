-- ===================================================================
-- MIGRACIÓN V8: Agregar columna fecha_movimiento a movimientos_historial (H2)
-- Sistema de Gestión de Inventarios
-- ===================================================================

-- Agregar columna fecha_movimiento a la tabla movimientos_historial
ALTER TABLE movimientos_historial 
ADD COLUMN fecha_movimiento TIMESTAMP;