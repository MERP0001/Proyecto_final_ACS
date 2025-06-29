-- ===================================================================
-- MIGRACIÓN V1: Crear tabla productos para PostgreSQL
-- Sistema de Gestión de Inventarios
-- ===================================================================

-- Crear tabla productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(12,2) NOT NULL CHECK (precio > 0),
    cantidad_inicial INTEGER NOT NULL CHECK (cantidad_inicial >= 0),
    cantidad_actual INTEGER NOT NULL CHECK (cantidad_actual >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    unidad_medida VARCHAR(50) DEFAULT 'UNIDAD',
    sku VARCHAR(100) UNIQUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_productos_nombre ON productos(nombre);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_precio ON productos(precio);
CREATE INDEX idx_productos_activo ON productos(activo);
CREATE INDEX idx_productos_sku ON productos(sku);

-- Crear secuencia para Hibernate Envers (INCREMENT BY 50 requerido)
CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;

-- Crear tabla de revisiones para Hibernate Envers
CREATE TABLE revinfo (
    rev INTEGER NOT NULL DEFAULT nextval('revinfo_seq') PRIMARY KEY,
    revtstmp BIGINT
);

-- Crear tabla de auditoría para Hibernate Envers
CREATE TABLE productos_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    nombre VARCHAR(100),
    descripcion VARCHAR(500),
    categoria VARCHAR(50),
    precio DECIMAL(12,2),
    cantidad_inicial INTEGER,
    cantidad_actual INTEGER,
    activo BOOLEAN,
    unidad_medida VARCHAR(50),
    sku VARCHAR(100),
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    version BIGINT,
    
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- Insertar datos de prueba para desarrollo
INSERT INTO productos (nombre, descripcion, categoria, precio, cantidad_inicial, cantidad_actual, sku) VALUES
('Laptop Dell Inspiron 15', 'Laptop Dell Inspiron 15 con procesador Intel Core i5, 8GB RAM, 256GB SSD', 'Electrónicos', 750.50, 10, 10, 'DELL-INSP-15-001'),
('Mouse Logitech MX Master 3', 'Mouse inalámbrico ergonómico para productividad', 'Periféricos', 99.99, 25, 23, 'LOG-MX-MASTER-001'),
('Monitor Samsung 27" 4K', 'Monitor Samsung de 27 pulgadas con resolución 4K UHD', 'Monitores', 299.99, 8, 7, 'SAM-MON-27-4K-001'),
('Teclado Mecánico Razer BlackWidow', 'Teclado mecánico gaming con switches Cherry MX', 'Periféricos', 129.99, 15, 12, 'RAZ-BW-MEC-001'),
('SSD Samsung 970 EVO 1TB', 'Disco sólido NVMe M.2 de 1TB con alta velocidad', 'Almacenamiento', 149.99, 20, 18, 'SAM-SSD-970-1TB-001');

-- Comentarios explicativos
COMMENT ON TABLE productos IS 'Tabla principal para almacenar información de productos del inventario';
COMMENT ON COLUMN productos.cantidad_inicial IS 'Cantidad inicial cuando se agregó el producto al inventario';
COMMENT ON COLUMN productos.cantidad_actual IS 'Cantidad actual disponible en inventario';
COMMENT ON COLUMN productos.sku IS 'Stock Keeping Unit - código único de identificación del producto';
COMMENT ON COLUMN productos.version IS 'Campo para control de concurrencia optimista'; 