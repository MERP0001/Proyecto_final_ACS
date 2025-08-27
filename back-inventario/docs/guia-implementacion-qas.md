# 🧪 Guía Completa de Implementación de Pruebas QAS

## 📋 Resumen Ejecutivo

Este documento describe la implementación completa de pruebas de **Aseguramiento de la Calidad del Software (QAS)** para el Sistema de Gestión de Inventarios, incluyendo:

- ✅ **Pruebas de Aceptación** con Cucumber (BDD)
- ⚡ **Pruebas de Estrés** con JMeter
- 🔄 **Pruebas de Regresión** automatizadas
- 📊 **Reportes consolidados** y métricas de calidad

---

## 🎯 Tipos de Pruebas Implementadas

### 1. **Pruebas de Aceptación (Cucumber BDD)**

#### Características:

- **Lenguaje:** Gherkin en español
- **Framework:** Cucumber 7.15.0 + Spring Boot Test
- **Cobertura:** Escenarios de negocio críticos

#### Archivos implementados:

```
src/test/resources/features/
├── gestion-productos.feature      # CRUD completo de productos
├── gestion-categorias.feature     # Gestión de categorías
├── autenticacion.feature          # Sistema de autenticación JWT
├── historial-movimientos.feature  # Trazabilidad de stock
├── regresion-suite.feature        # Suite de regresión crítica
└── producto-simple.feature        # Pruebas básicas (existente)
```

#### Steps implementados:

```
src/test/java/.../cucumber/steps/
├── ProductoSteps.java             # Steps para productos (existente)
├── CategoriaSteps.java            # Steps para categorías
├── AutenticacionSteps.java        # Steps para JWT/Auth
└── ProductoSimpleSteps.java       # Steps básicos (existente)
```

#### Comandos de ejecución:

```bash
# Todas las pruebas de aceptación
./gradlew cucumber

# Solo pruebas de productos
./gradlew cucumber -Dcucumber.filter.tags="@productos"

# Solo pruebas de regresión
./gradlew regressionTests
```

### 2. **Pruebas de Estrés (JMeter)**

#### Características:

- **Herramienta:** Apache JMeter 5.6+
- **Tipos:** Carga normal, Estrés intensivo, Pruebas de pico
- **Métricas:** Tiempo respuesta, throughput, tasa de error

#### Archivos implementados:

```
src/test/jmeter/
└── stress-test-plan.jmx           # Plan completo de pruebas

Scripts de automatización:
├── run-stress-tests.sh            # Script para Linux/Mac
└── run-stress-tests.ps1           # Script para Windows
```

#### Escenarios de prueba:

**🔥 Carga Normal:**

- **Usuarios:** 50 concurrentes
- **Duración:** 5 minutos
- **Objetivo:** Tiempo respuesta < 500ms

**⚡ Estrés Intensivo:**

- **Usuarios:** 200 concurrentes
- **Duración:** 10 minutos
- **Objetivo:** Sistema estable bajo presión

**🚀 Prueba de Pico:**

- **Usuarios:** 500 concurrentes
- **Duración:** 1 minuto
- **Objetivo:** Recuperación rápida post-pico

#### Comandos de ejecución:

```powershell
# Windows
.\run-stress-tests.ps1

# Opciones específicas
.\run-stress-tests.ps1 -Threads 100 -Duration 600
.\run-stress-tests.ps1 -Host "192.168.1.100"
```

### 3. **Pruebas de Regresión**

#### Características:

- **Automatización:** Scripts PowerShell/Bash
- **Cobertura:** Funcionalidades críticas del sistema
- **Integración:** Backend + Frontend

#### Archivos implementados:

```
Scripts de regresión:
├── run-regression-tests.sh        # Script para Linux/Mac
└── run-regression-tests.ps1       # Script para Windows

Estructura de reportes:
target/regression-reports/
├── current/                       # Ejecución actual
├── previous/                      # Ejecución anterior
├── logs/                         # Logs detallados
└── screenshots/                  # Capturas de fallos
```

#### Pruebas incluidas:

- ✅ **Smoke Tests:** Verificación básica del sistema
- 🔐 **Autenticación:** Login, JWT, autorización
- 📦 **CRUD Productos:** Operaciones básicas
- 🔍 **Búsquedas:** Filtros y paginación
- 📂 **Categorías:** Gestión de categorías
- 🌐 **API:** Endpoints críticos
- 🎨 **Frontend:** Flujos de usuario principales

#### Comandos de ejecución:

```powershell
# Regresión completa
.\run-regression-tests.ps1

# Solo backend
.\run-regression-tests.ps1 -BackendOnly

# Solo frontend
.\run-regression-tests.ps1 -FrontendOnly
```

---

## 🚀 Guía de Implementación Paso a Paso

### **PASO 1: Prerequisitos**

#### Instalar herramientas necesarias:

**JMeter:**

```bash
# Descargar desde: https://jmeter.apache.org/download_jmeter.cgi
# Extraer en C:\jmeter (Windows) o /opt/jmeter (Linux)

# Verificar instalación
C:\jmeter\bin\jmeter.bat --version
```

**Node.js y npm** (para frontend):

```bash
# Verificar instalación
node --version
npm --version
```

#### Configurar variables de entorno:

```bash
# Windows
$env:JMETER_HOME = "C:\jmeter"

# Linux/Mac
export JMETER_HOME="/opt/jmeter"
```

### **PASO 2: Configuración del Proyecto**

#### Verificar dependencias en build.gradle:

```gradle
// Ya incluidas en el proyecto
testImplementation 'io.cucumber:cucumber-java:7.15.0'
testImplementation 'io.cucumber:cucumber-spring:7.15.0'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
```

#### Estructura de directorios:

```
Verificar que existan:
├── src/test/resources/features/        ✅ Creado
├── src/test/jmeter/                   ✅ Creado
├── src/test/regression/               ✅ Creado
└── target/                           ✅ Auto-generado
```

### **PASO 3: Ejecución de Pruebas**

#### 3.1 Pruebas de Aceptación

```bash
# 1. Ejecutar todas las pruebas Cucumber
./gradlew cucumber

# 2. Ver reportes
# Abrir: target/cucumber-reports/Cucumber.html

# 3. Ejecutar por tags específicos
./gradlew cucumber -Dcucumber.filter.tags="@productos"
```

#### 3.2 Pruebas de Estrés

```powershell
# 1. Iniciar el backend
./gradlew bootRun

# 2. En otra terminal, ejecutar JMeter
.\run-stress-tests.ps1

# 3. Seleccionar tipo de prueba en el menú
# 4. Ver reportes en: target/jmeter-reports/
```

#### 3.3 Pruebas de Regresión

```powershell
# 1. Asegurar que backend y frontend estén funcionando
./gradlew bootRun
cd front-inventario && npm run dev

# 2. Ejecutar regresión
.\run-regression-tests.ps1

# 3. Ver reporte: target/regression-reports/regression_summary_*.html
```

### **PASO 4: Interpretación de Resultados**

#### Métricas clave a evaluar:

**Pruebas de Aceptación:**

- ✅ **Éxito:** Todos los escenarios pasan
- ❌ **Fallo:** Revisar logs de Cucumber para detalles

**Pruebas de Estrés:**

- ⏱️ **Tiempo de respuesta:** < 1000ms para 95% de requests
- 🚀 **Throughput:** > 100 requests/segundo
- ❌ **Tasa de error:** < 1%

**Pruebas de Regresión:**

- 🟢 **PASS:** Sistema listo para deploy
- 🔴 **FAIL:** NO proceder con deploy
- 🟡 **PARTIAL:** Revisar componentes fallidos

---

## 📊 Reportes y Métricas

### **Dashboard Consolidado**

Ejecutar para reporte completo:

```bash
./gradlew qasFullSuite
```

#### Reportes generados:

1. **JUnit:** `build/reports/tests/test/index.html`
2. **Cucumber:** `target/cucumber-reports/Cucumber.html`
3. **JaCoCo:** `build/reports/jacoco/test/html/index.html`
4. **QAS Consolidado:** `build/reports/qas/index.html`

### **Métricas de Calidad Objetivo**

| Métrica              | Objetivo         | Herramienta |
| -------------------- | ---------------- | ----------- |
| Cobertura de Código  | ≥ 80%            | JaCoCo      |
| Tiempo Respuesta API | < 500ms promedio | JMeter      |
| Tasa de Error        | < 1%             | JMeter      |
| Pruebas Pasando      | 100% críticas    | Cucumber    |
| Disponibilidad       | ≥ 99%            | Monitoreo   |

---

## 🔧 Solución de Problemas

### **Problemas Comunes:**

#### JMeter no inicia:

```bash
# Verificar Java
java -version

# Verificar JMETER_HOME
echo $JMETER_HOME  # Linux/Mac
echo $env:JMETER_HOME  # Windows
```

#### Cucumber no encuentra features:

```bash
# Verificar estructura
ls src/test/resources/features/

# Ejecutar con debug
./gradlew cucumber --debug
```

#### Fallos de regresión:

```bash
# Ver logs detallados
Get-Content target/regression-reports/logs/*.log

# Ejecutar componente individual
.\gradlew test --tests "*RegressionTestSuite"
```

### **Comandos de Diagnóstico:**

```bash
# Estado del sistema
curl http://localhost:8080/actuator/health

# Logs de aplicación
tail -f logs/application.log

# Pruebas individuales
./gradlew test --tests "*ProductoSteps"
```

---

## 📈 Integración con CI/CD

### **Pipeline recomendado:**

```yaml
stages:
  - build: "./gradlew build"
  - test: "./gradlew test"
  - acceptance: "./gradlew cucumber"
  - regression: "./run-regression-tests.ps1"
  - stress: "./run-stress-tests.ps1" (opcional)
  - deploy: "si todo pasa"
```

### **Criterios de gate:**

- ✅ Build exitoso
- ✅ Tests unitarios 100%
- ✅ Cobertura ≥ 80%
- ✅ Regresión PASS
- ✅ Pruebas críticas PASS

---

## 🎯 Próximos Pasos

### **Mejoras Recomendadas:**

1. **📊 Monitoreo:** Implementar Prometheus + Grafana
2. **🔄 CI/CD:** Integrar con Jenkins/GitHub Actions
3. **📱 Móvil:** Pruebas en dispositivos móviles
4. **🌐 Cross-browser:** Ampliar cobertura de navegadores
5. **💾 Datos:** Pruebas con datasets grandes

### **Mantenimiento:**

- 🔄 **Semanal:** Ejecutar suite completa de regresión
- 📅 **Mensual:** Revisar y actualizar escenarios de Cucumber
- 🔍 **Trimestral:** Análisis de métricas y optimización
- 📚 **Anual:** Revisión completa de estrategia QAS

---

## 📞 Soporte y Contacto

**Documentación adicional:**

- [Cucumber Reference](https://cucumber.io/docs/)
- [JMeter User Manual](https://jmeter.apache.org/usermanual/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

**Estructura del equipo QAS:**

- **QA Lead:** Estrategia y planificación
- **Automation Engineer:** Implementación de scripts
- **Performance Tester:** Optimización y métricas
- **Developer:** Integración con desarrollo

---

_📝 Este documento se actualiza continuamente. Última revisión: $(Get-Date)_
