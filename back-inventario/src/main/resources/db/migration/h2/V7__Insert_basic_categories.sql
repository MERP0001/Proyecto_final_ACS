-- ===================================================================
-- MIGRACIÓN V7: Insertar categorías básicas (H2)
-- Incluye las categorías más comunes para evitar errores de categorías no encontradas
-- ===================================================================

-- Insertar categorías básicas usando MERGE para evitar duplicados (H2 syntax)
MERGE INTO categorias (nombre, descripcion, activo, created_at, version) 
KEY(nombre) VALUES 
('Electrónicos', 'Productos electrónicos y tecnológicos', true, CURRENT_TIMESTAMP, 0),
('Laptops', 'Computadoras portátiles y accesorios', true, CURRENT_TIMESTAMP, 0),
('Smartphones', 'Teléfonos móviles y accesorios', true, CURRENT_TIMESTAMP, 0),
('Tablets', 'Tabletas y accesorios', true, CURRENT_TIMESTAMP, 0),
('Accesorios', 'Accesorios diversos para dispositivos', true, CURRENT_TIMESTAMP, 0),
('Hogar', 'Productos para el hogar', true, CURRENT_TIMESTAMP, 0),
('Oficina', 'Suministros y equipos de oficina', true, CURRENT_TIMESTAMP, 0),
('Gaming', 'Productos para videojuegos', true, CURRENT_TIMESTAMP, 0),
('Audio', 'Equipos de audio y sonido', true, CURRENT_TIMESTAMP, 0),
('Ropa', 'Prendas de vestir', true, CURRENT_TIMESTAMP, 0);

-- H2 no soporta ON CONFLICT, así que usamos MERGE o simplemente insertamos
-- Si ya existen, H2 fallará pero eso está bien porque significa que ya tenemos datos

-- Verificar inserción (opcional para H2)
-- SELECT 'Categorías insertadas:' as mensaje, COUNT(*) as total FROM categorias WHERE activo = true;