-- ===================================================================
-- SCRIPT DE INICIALIZACIÓN POSTGRESQL
-- Sistema de Gestión de Inventarios
-- Ejecutado automáticamente al crear el contenedor PostgreSQL
-- ===================================================================

-- Verificar que estamos en la base de datos correcta
\c inventario_db;

-- Mostrar información de la base de datos
SELECT version();
SELECT current_database();

-- Configurar zona horaria por defecto
SET timezone = 'America/Santo_Domingo';

-- Crear extensiones útiles (opcional)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Configuraciones de rendimiento (opcional)
-- Estas configuraciones se pueden ajustar según los recursos disponibles
-- ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
-- ALTER SYSTEM SET max_connections = 100;
-- ALTER SYSTEM SET shared_buffers = '256MB';
-- ALTER SYSTEM SET effective_cache_size = '1GB';
-- ALTER SYSTEM SET maintenance_work_mem = '64MB';
-- ALTER SYSTEM SET checkpoint_completion_target = 0.9;
-- ALTER SYSTEM SET wal_buffers = '16MB';
-- ALTER SYSTEM SET default_statistics_target = 100;
-- ALTER SYSTEM SET random_page_cost = 1.1;
-- ALTER SYSTEM SET effective_io_concurrency = 200;

-- Verificar configuraciones aplicadas
SHOW timezone;
SELECT * FROM pg_extension WHERE extname IN ('uuid-ossp', 'pg_stat_statements');

-- Mensaje de confirmación  
\echo 'Base de datos inicializada correctamente para Sistema de Gestión de Inventarios'
\echo 'Zona horaria configurada:', CURRENT_SETTING('timezone')
\echo 'Extensiones instaladas correctamente' 