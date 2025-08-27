-- ===================================================================
-- MIGRACIÓN V5: Crear tabla categorias para H2 (Testing)
-- Sistema de Gestión de Inventarios
-- ===================================================================

-- Crear tabla categorias
CREATE TABLE categorias (
    id BIGINT IDENTITY PRIMARY KEY,
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

-- Insertar categorías de prueba para testing
INSERT INTO categorias (nombre, descripcion) VALUES
('Electrónicos', 'Productos electrónicos y tecnológicos'),
('Gaming', 'Productos para videojuegos y entretenimiento'),
('Hogar', 'Productos para el hogar y decoración'),
('Oficina', 'Suministros y equipos de oficina'),
('Deportes', 'Artículos deportivos y fitness'),
('Smartphones', 'Teléfonos móviles y accesorios'),
('Laptops', 'Computadoras portátiles y accesorios'),
('Componentes PC', 'Componentes para computadoras'),
('Audio', 'Equipos de audio y sonido'),
('Almacenamiento', 'Dispositivos de almacenamiento');
