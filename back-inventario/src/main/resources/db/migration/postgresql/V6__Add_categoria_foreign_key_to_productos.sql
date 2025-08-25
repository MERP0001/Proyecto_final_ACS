-- Migración: Cambiar relación de categoría de String a Foreign Key
-- Autor: Sistema
-- Fecha: 2025-08-24

-- Paso 1: Insertar categorías únicas existentes en la tabla categorias
INSERT INTO categorias (nombre, descripcion, activo, created_at, updated_at)
SELECT DISTINCT 
    categoria as nombre,
    'Categoría migrada automáticamente desde productos' as descripcion,
    true as activo,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP as updated_at
FROM productos 
WHERE categoria IS NOT NULL 
  AND categoria != ''
  AND NOT EXISTS (
    SELECT 1 FROM categorias c WHERE LOWER(c.nombre) = LOWER(productos.categoria)
  );

-- Paso 2: Agregar columna categoria_id a la tabla productos
ALTER TABLE productos ADD COLUMN categoria_id BIGINT;

-- Paso 3: Agregar índice para mejorar rendimiento
CREATE INDEX idx_productos_categoria_id ON productos(categoria_id);

-- Paso 4: Actualizar productos con categoria_id basado en el nombre de categoria
UPDATE productos 
SET categoria_id = (
    SELECT c.id 
    FROM categorias c 
    WHERE LOWER(c.nombre) = LOWER(productos.categoria)
    LIMIT 1
)
WHERE categoria IS NOT NULL AND categoria != '';

-- Paso 5: Agregar constraint de foreign key
ALTER TABLE productos 
ADD CONSTRAINT fk_productos_categoria 
FOREIGN KEY (categoria_id) REFERENCES categorias(id);

-- Paso 6: Verificar que todos los productos tienen categoria_id (los que tenían categoria)
-- Los productos sin categoría mantendrán categoria_id = NULL

-- Paso 7: Crear índice compuesto para consultas frecuentes
CREATE INDEX idx_productos_categoria_activo ON productos(categoria_id, activo);

-- Paso 8: Agregar comentarios a las columnas
COMMENT ON COLUMN productos.categoria_id IS 'Foreign key hacia la tabla categorias';
COMMENT ON COLUMN productos.categoria IS 'Columna legacy - mantener temporalmente para rollback';

-- Nota: No eliminamos la columna 'categoria' en esta migración por seguridad
-- Se eliminará en una migración posterior después de validar que todo funciona correctamente
