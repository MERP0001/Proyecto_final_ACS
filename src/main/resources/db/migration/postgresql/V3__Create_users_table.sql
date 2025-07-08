-- Crear tabla de usuarios para autenticación JWT
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
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
    '$2a$12$uXUuZYdkOxgaCGFZ3/2CPOx2Kg1wNeO8DnZGqM4N8m5WFi4bJAwGS', 
    'admin@inventarios.com', 
    'Administrador del Sistema', 
    'ADMINISTRADOR', 
    true
);

-- Comentarios sobre la tabla
COMMENT ON TABLE usuarios IS 'Tabla de usuarios del sistema de inventarios';
COMMENT ON COLUMN usuarios.username IS 'Nombre de usuario único para login';
COMMENT ON COLUMN usuarios.password IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN usuarios.role IS 'Rol del usuario (ADMINISTRADOR)';
COMMENT ON COLUMN usuarios.activo IS 'Indica si el usuario está activo en el sistema'; 