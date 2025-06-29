# Plan de Proyecto - Sistema de Gestión de Inventarios

## 1. Información General
- **Proyecto**: Sistema de Gestión de Inventarios con QAS
- **Versión**: 1.0
- **Fecha Inicio**: Diciembre 2024
- **Tecnología Principal**: Spring Boot 3.5.3

## 2. Alcance del Proyecto

### 2.1 Objetivos
- Desarrollar un sistema de gestión de inventarios para pequeña empresa
- Implementar todas las etapas del ciclo de vida QAS
- Cumplir con altos estándares de calidad, seguridad y usabilidad

### 2.2 Funcionalidades Incluidas
- ✅ **Gestión de Productos (CRUD)**
  - Agregar producto (nombre, descripción, categoría, precio, cantidad)
  - Editar producto existente
  - Eliminar producto
  - Visualizar productos con búsqueda y filtrado

- ✅ **API de Integración**
  - REST API para integración con otros sistemas
  - Autenticación JWT
  - Documentación OpenAPI/Swagger

- ✅ **Roles y Autenticación**
  - Rol Administrador (acceso completo)
  - Autenticación OAuth2/JWT

### 2.3 Funcionalidades Excluidas
- ❌ Control de Stock (no requerido)
- ❌ Interfaz de Usuario web (no requerido)
- ❌ Roles Empleado/Invitado (no requerido)

## 3. Entregables

### 3.1 Código Fuente
- Backend Spring Boot con API REST
- Configuración de base de datos
- Migraciones con Flyway
- Configuración Docker

### 3.2 Pruebas
- Pruebas unitarias (JUnit)
- Pruebas de aceptación (Cucumber)
- Pruebas de navegadores (Playwright)
- Pruebas de compatibilidad

### 3.3 Documentación
- ✅ Plan de proyecto
- Documentación de requisitos funcionales/no funcionales
- Guía de pruebas
- Manual de instalación

### 3.4 Infraestructura
- Contenedorización (Docker)
- Migración de BD (Flyway)
- Configuración de entornos

## 4. Cronograma de Desarrollo

### Fase 1: Configuración y Planificación (Semana 1)
- [x] Análisis de requisitos
- [ ] Configuración del entorno de desarrollo
- [ ] Configuración de dependencias
- [ ] Estructura básica del proyecto

### Fase 2: Desarrollo Core (Semana 2-3)
- [ ] Modelo de datos (Entidades JPA)
- [ ] Capa de persistencia (Repositories)
- [ ] Lógica de negocio (Services)
- [ ] API REST (Controllers)
- [ ] Autenticación JWT

### Fase 3: Pruebas y QAS (Semana 4)
- [ ] Configuración Cucumber
- [ ] Pruebas de aceptación
- [ ] Configuración Playwright
- [ ] Pruebas de compatibilidad
- [ ] Métricas de calidad

### Fase 4: Infraestructura y Despliegue (Semana 5)
- [ ] Configuración Docker
- [ ] Migraciones Flyway
- [ ] Documentación completa
- [ ] Testing final

## 5. Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.5.3
- **Base de Datos**: PostgreSQL (Producción), H2 (Desarrollo)
- **Migraciones**: Flyway
- **Auditoría**: Hibernate Envers
- **Autenticación**: Spring Security + JWT

### Testing
- **Unitarias**: JUnit 5 + TestNG
- **Aceptación**: Cucumber
- **Navegadores**: Playwright
- **API Testing**: MockMvc + TestContainers

### Infraestructura
- **Contenedorización**: Docker + Docker Compose
- **Build**: Gradle
- **Documentación API**: OpenAPI 3 (Swagger)

## 6. Gestión de Riesgos

### Riesgos Identificados
1. **Riesgo**: Complejidad de configuración OAuth2/JWT
   - **Probabilidad**: Media
   - **Impacto**: Alto
   - **Mitigación**: Implementar autenticación básica primero, JWT después

2. **Riesgo**: Problemas de compatibilidad con Playwright
   - **Probabilidad**: Media
   - **Impacto**: Medio
   - **Mitigación**: Configurar entorno de testing aislado

3. **Riesgo**: Configuración compleja de Docker
   - **Probabilidad**: Baja
   - **Impacto**: Medio
   - **Mitigación**: Usar Docker Compose con configuraciones estándar

## 7. Indicadores de Calidad

### Métricas Objetivo
- **Cobertura de Pruebas**: ≥ 80%
- **Tiempo de Respuesta API**: < 200ms
- **Disponibilidad**: ≥ 99%
- **Seguridad**: Sin vulnerabilidades críticas

### Herramientas de Monitoreo
- SonarQube (análisis de código)
- JaCoCo (cobertura de pruebas)
- Spring Boot Actuator (métricas runtime)

## 8. Criterios de Aceptación

### Funcionales
- ✅ CRUD completo de productos funcional
- ✅ API REST documentada y probada
- ✅ Autenticación JWT implementada
- ✅ Búsqueda y filtrado de productos

### No Funcionales
- ✅ Todas las pruebas QAS pasando
- ✅ Documentación completa
- ✅ Sistema containerizado
- ✅ Migraciones de BD funcionando

## 9. Equipo y Responsabilidades

### Roles del Proyecto
- **Desarrollador Backend**: Implementación API y lógica de negocio
- **QA Engineer**: Configuración y ejecución de pruebas
- **DevOps**: Configuración Docker y despliegue

*Nota: En equipos de 2 personas, cada miembro asume múltiples roles* 