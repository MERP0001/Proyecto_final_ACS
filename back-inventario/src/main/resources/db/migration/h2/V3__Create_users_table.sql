-- Crear tabla de usuarios para autenticación JWT (H2)
CREATE TABLE usuarios (
    id BIGINT IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre_completo VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'ADMINISTRADOR',
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- Insertar usuario administrador por defecto
-- Contraseña: admin123 (hasheada con BCrypt)
INSERT INTO usuarios (username, password, email, nombre_completo, role, activo) 
VALUES (
    'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFiO5fKyTMR7Ux8cNy1Rjcr', 
    'admin@inventarios.com', 
    'Administrador del Sistema', 
    'ADMINISTRADOR', 
    true
); 