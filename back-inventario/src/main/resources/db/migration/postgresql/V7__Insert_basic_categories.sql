-- ===================================================================
-- MIGRACIÓN V7: Insertar categorías básicas
-- Incluye las categorías más comunes para evitar errores de categorías no encontradas
-- ===================================================================

-- Insertar categorías básicas usando los nombres correctos de columnas
INSERT INTO categorias (nombre, descripcion, activo, created_at, version) VALUES
('Electrónicos', 'Productos electrónicos y tecnológicos', true, CURRENT_TIMESTAMP, 0),
('Laptops', 'Computadoras portátiles y accesorios', true, CURRENT_TIMESTAMP, 0),
('Smartphones', 'Teléfonos móviles y accesorios', true, CURRENT_TIMESTAMP, 0),
('Tablets', 'Tabletas y accesorios', true, CURRENT_TIMESTAMP, 0),
('Accesorios', 'Accesorios diversos para dispositivos', true, CURRENT_TIMESTAMP, 0),
('Hogar', 'Productos para el hogar', true, CURRENT_TIMESTAMP, 0),
('Oficina', 'Suministros y equipos de oficina', true, CURRENT_TIMESTAMP, 0),
('Gaming', 'Productos para videojuegos', true, CURRENT_TIMESTAMP, 0),
('Audio', 'Equipos de audio y sonido', true, CURRENT_TIMESTAMP, 0),
('Ropa', 'Prendas de vestir', true, CURRENT_TIMESTAMP, 0)
ON CONFLICT (nombre) DO NOTHING;

-- Verificar inserción
SELECT 'Categorías insertadas:' as mensaje, COUNT(*) as total FROM categorias WHERE activo = true;
