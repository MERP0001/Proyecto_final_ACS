# Docker - Sistema de Gestión de Inventarios

## 🐳 Configuración Docker Completa

Este proyecto incluye una configuración completa de Docker con:
- **PostgreSQL 16** como base de datos principal
- **Spring Boot** aplicación containerizada
- **pgAdmin** para administración de base de datos
- **Health checks** y monitoring
- **Volúmenes persistentes** para datos

## 📋 Prerrequisitos

- Docker Desktop instalado y ejecutándose
- Docker Compose v3.8 o superior
- Puertos disponibles: 8080, 5432, 5050

## 🚀 Inicio Rápido

### 1. Clonar y construir
```bash
# Clonar el repositorio
git clone <tu-repositorio>
cd Proyecto-final

# Construir y levantar todos los servicios
docker-compose up --build
```

### 2. Verificar servicios
```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs de la aplicación
docker-compose logs -f app

# Ver logs de PostgreSQL
docker-compose logs -f postgres
```

### 3. Acceder a los servicios

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Aplicación** | http://localhost:8080 | - |
| **API Docs** | http://localhost:8080/swagger-ui.html | - |
| **Health Check** | http://localhost:8080/api/actuator/health | - |
| **pgAdmin** | http://localhost:5050 | admin@inventario.com / admin123 |
| **PostgreSQL** | localhost:5432 | postgres / postgres123 |

## 🔧 Comandos Útiles

### Gestión de contenedores
```bash
# Levantar servicios en background
docker-compose up -d

# Parar todos los servicios
docker-compose down

# Parar y eliminar volúmenes (CUIDADO: elimina datos)
docker-compose down -v

# Reconstruir aplicación
docker-compose up --build app

# Ver logs en tiempo real
docker-compose logs -f
```

### Gestión de base de datos
```bash
# Conectar a PostgreSQL directamente
docker-compose exec postgres psql -U postgres -d inventario_db

# Backup de base de datos
docker-compose exec postgres pg_dump -U postgres inventario_db > backup.sql

# Restaurar backup
docker-compose exec -T postgres psql -U postgres -d inventario_db < backup.sql
```

### Debugging
```bash
# Acceder al contenedor de la aplicación
docker-compose exec app bash

# Ver variables de entorno
docker-compose exec app env

# Verificar conectividad de red
docker-compose exec app nc -zv postgres 5432
```

## 🔒 Configuración de Seguridad

### Variables de Entorno Importantes
```bash
# Crear archivo .env en la raíz del proyecto
DATABASE_PASSWORD=tu_password_seguro
JWT_SECRET=tu_jwt_secret_muy_largo_y_seguro
PGADMIN_DEFAULT_PASSWORD=tu_password_pgadmin
```

### Configuración de Producción
Para producción, asegúrate de:
1. Cambiar todas las contraseñas por defecto
2. Usar secretos de Docker o variables de entorno seguras
3. Configurar certificados SSL/TLS
4. Restringir acceso a pgAdmin

## 📊 Monitoring y Health Checks

### Health Checks Configurados
- **PostgreSQL**: Verifica conexión cada 30s
- **Aplicación**: Verifica endpoint /actuator/health cada 30s

### Métricas Disponibles
- http://localhost:8080/api/actuator/health
- http://localhost:8080/api/actuator/info
- http://localhost:8080/api/actuator/metrics

### Opcional: Prometheus + Grafana
Descomenta las secciones de monitoring en `docker-compose.yml` para habilitar:
```bash
docker-compose up -d prometheus grafana
```

## 🗂️ Estructura de Volúmenes

```
volumes/
├── postgres_data/     # Datos de PostgreSQL
├── pgadmin_data/      # Configuración de pgAdmin
└── app_logs/          # Logs de la aplicación
```

## 🔧 Personalización

### Configurar Variables de Entorno
Edita `docker-compose.yml` o crea un archivo `.env`:

```env
POSTGRES_DB=mi_base_datos
POSTGRES_USER=mi_usuario
POSTGRES_PASSWORD=mi_password
JWT_SECRET=mi_jwt_secret_super_seguro
```

### Cambiar Puertos
```yaml
services:
  app:
    ports:
      - "9090:8080"  # Cambiar puerto externo
  postgres:
    ports:
      - "5433:5432"  # Cambiar puerto de PostgreSQL
```

## 🐛 Troubleshooting

### Problema: Puerto ya en uso
```bash
# Encontrar qué está usando el puerto
netstat -tulpn | grep :8080

# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"
```

### Problema: Base de datos no se conecta
```bash
# Verificar estado de PostgreSQL
docker-compose logs postgres

# Verificar conectividad
docker-compose exec app ping postgres
```

### Problema: Aplicación no inicia
```bash
# Ver logs detallados
docker-compose logs -f app

# Verificar variables de entorno
docker-compose exec app env | grep DATABASE
```

### Problema: Volúmenes corruptos
```bash
# Eliminar volúmenes y recrear (CUIDADO: elimina datos)
docker-compose down -v
docker volume prune
docker-compose up --build
```

## 📝 Notas Importantes

1. **Datos Persistentes**: Los datos de PostgreSQL se guardan en volúmenes Docker
2. **Primera Ejecución**: La primera vez tarda más porque construye la imagen
3. **Desarrollo**: Para desarrollo usa `application-dev.properties` con H2
4. **Producción**: Este Docker está optimizado para producción con PostgreSQL
5. **Backup**: Haz backups regulares de los volúmenes de datos

## 🆘 Soporte

Si encuentras problemas:
1. Revisa los logs: `docker-compose logs -f`
2. Verifica el estado: `docker-compose ps`
3. Reinicia servicios: `docker-compose restart`
4. En último caso: `docker-compose down && docker-compose up --build` 