-- Migración H2: Cambiar relación de categoría de String a Foreign Key
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
  AND categoria <> ''
  AND NOT EXISTS (
    SELECT 1 FROM categorias c WHERE UPPER(c.nombre) = UPPER(productos.categoria)
  );

-- Paso 2: Agregar columna categoria_id a la tabla productos
ALTER TABLE productos ADD COLUMN categoria_id BIGINT;

-- Paso 3: Actualizar productos con categoria_id basado en el nombre de categoria
UPDATE productos 
SET categoria_id = (
    SELECT c.id 
    FROM categorias c 
    WHERE UPPER(c.nombre) = UPPER(productos.categoria)
    LIMIT 1
)
WHERE categoria IS NOT NULL AND categoria <> '';

-- Paso 4: Agregar constraint de foreign key
ALTER TABLE productos 
ADD CONSTRAINT fk_productos_categoria 
FOREIGN KEY (categoria_id) REFERENCES categorias(id);

-- Nota: H2 no soporta COMMENT ON COLUMN, se omite
-- Nota: Los índices adicionales no son críticos para testing
