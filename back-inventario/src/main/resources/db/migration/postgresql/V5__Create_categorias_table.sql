-- ===================================================================
-- MIGRACIÓN V5: Crear tabla categorias para PostgreSQL
-- Sistema de Gestión de Inventarios
-- ===================================================================

-- Crear tabla categorias
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_categorias_nombre ON categorias(nombre);
CREATE INDEX idx_categorias_activo ON categorias(activo);
CREATE INDEX idx_categorias_nombre_activo ON categorias(nombre, activo);

-- Insertar categorías basadas en los datos existentes de productos
INSERT INTO categorias (nombre, descripcion) 
SELECT DISTINCT 
    categoria as nombre,
    CONCAT('Categoría de ', categoria) as descripcion
FROM productos 
WHERE categoria IS NOT NULL AND categoria != ''
ORDER BY categoria;

-- Insertar categorías adicionales comunes si no existen
INSERT INTO categorias (nombre, descripcion) 
SELECT 'Electrónicos', 'Productos electrónicos y tecnológicos'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Electrónicos');

INSERT INTO categorias (nombre, descripcion) 
SELECT 'Hogar', 'Productos para el hogar y decoración'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Hogar');

INSERT INTO categorias (nombre, descripcion) 
SELECT 'Deportes', 'Artículos deportivos y fitness'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Deportes');

INSERT INTO categorias (nombre, descripcion) 
SELECT 'Oficina', 'Suministros y equipos de oficina'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Oficina');

-- Comentarios para documentación
COMMENT ON TABLE categorias IS 'Tabla que almacena las categorías de productos del sistema de inventarios';
COMMENT ON COLUMN categorias.id IS 'Identificador único de la categoría';
COMMENT ON COLUMN categorias.nombre IS 'Nombre único de la categoría';
COMMENT ON COLUMN categorias.descripcion IS 'Descripción detallada de la categoría';
COMMENT ON COLUMN categorias.activo IS 'Indica si la categoría está activa en el sistema';
COMMENT ON COLUMN categorias.created_at IS 'Fecha y hora de creación de la categoría';
COMMENT ON COLUMN categorias.updated_at IS 'Fecha y hora de última modificación';
COMMENT ON COLUMN categorias.version IS 'Versión para control de concurrencia optimista';
