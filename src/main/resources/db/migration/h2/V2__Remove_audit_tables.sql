-- ===================================================================
-- MIGRACIÓN V2: Remover tablas de auditoría temporalmente
-- Sistema de Gestión de Inventarios
-- ===================================================================
 
-- Eliminar tablas de auditoría que causan problemas con H2
DROP TABLE IF EXISTS productos_AUD;
DROP TABLE IF EXISTS REVINFO;
DROP SEQUENCE IF EXISTS revinfo_seq; 