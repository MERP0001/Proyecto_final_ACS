#!/bin/bash

# =============================================================================
# Script de Ejecución de Pruebas de Regresión
# Sistema de Gestión de Inventarios
# =============================================================================

# Variables de configuración
REPORTS_DIR="target/regression-reports"
SCREENSHOTS_DIR="$REPORTS_DIR/screenshots"
LOGS_DIR="$REPORTS_DIR/logs"
BACKUP_DIR="$REPORTS_DIR/backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Función para log
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

info() {
    echo -e "${PURPLE}[INFO]${NC} $1"
}

# Función para preparar entorno
setup_environment() {
    log "Preparando entorno para pruebas de regresión..."
    
    # Crear directorios
    mkdir -p "$REPORTS_DIR"
    mkdir -p "$SCREENSHOTS_DIR"
    mkdir -p "$LOGS_DIR"
    mkdir -p "$BACKUP_DIR"
    
    # Backup de reportes anteriores si existen
    if [ -d "$REPORTS_DIR/previous" ]; then
        mv "$REPORTS_DIR/previous" "$BACKUP_DIR/regression_$TIMESTAMP"
        log "Backup de reportes anteriores realizado"
    fi
    
    # Crear directorio para nueva ejecución
    mkdir -p "$REPORTS_DIR/current"
    
    success "Entorno preparado"
}

# Función para verificar estado del sistema
check_system_health() {
    log "Verificando estado del sistema..."
    
    # Verificar base de datos
    if ! ./gradlew test -Dtest="**/ProyectoFinalApplicationTests" > /dev/null 2>&1; then
        error "La aplicación no arranca correctamente"
        return 1
    fi
    
    # Verificar endpoints básicos
    log "Verificando endpoints básicos..."
    if command -v curl &> /dev/null; then
        # Verificar health endpoint
        if curl -s "http://localhost:8080/actuator/health" | grep -q "UP"; then
            success "Sistema saludable"
        else
            warning "Health endpoint no responde - continuando"
        fi
    fi
    
    return 0
}

# Función para ejecutar pruebas de regresión backend
run_backend_regression() {
    log "Ejecutando pruebas de regresión del backend..."
    
    local test_output="$LOGS_DIR/backend_regression_$TIMESTAMP.log"
    
    # Ejecutar pruebas específicas de regresión
    ./gradlew test \
        -Dtest.single=RegressionTestSuite \
        --info \
        > "$test_output" 2>&1
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        success "Pruebas de regresión backend PASARON ✅"
        
        # Copiar reportes
        if [ -d "build/reports/tests" ]; then
            cp -r build/reports/tests "$REPORTS_DIR/current/backend-junit"
        fi
        
        if [ -d "target/cucumber-reports" ]; then
            cp -r target/cucumber-reports "$REPORTS_DIR/current/backend-cucumber"
        fi
        
    else
        error "Pruebas de regresión backend FALLARON ❌"
        warning "Ver detalles en: $test_output"
    fi
    
    return $exit_code
}

# Función para ejecutar pruebas de regresión frontend
run_frontend_regression() {
    log "Ejecutando pruebas de regresión del frontend..."
    
    cd front-inventario || {
        error "No se puede acceder al directorio del frontend"
        return 1
    }
    
    local test_output="../$LOGS_DIR/frontend_regression_$TIMESTAMP.log"
    
    # Ejecutar pruebas críticas de Playwright
    npm test -- --grep="@regresion" > "$test_output" 2>&1 || \
    npx playwright test --grep="login|productos|logout" > "$test_output" 2>&1
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        success "Pruebas de regresión frontend PASARON ✅"
        
        # Copiar reportes de Playwright
        if [ -d "playwright-report" ]; then
            cp -r playwright-report "../$REPORTS_DIR/current/frontend-playwright"
        fi
        
        if [ -d "test-results" ]; then
            cp -r test-results "../$REPORTS_DIR/current/frontend-results"
        fi
        
    else
        error "Pruebas de regresión frontend FALLARON ❌"
        warning "Ver detalles en: $test_output"
    fi
    
    cd ..
    return $exit_code
}

# Función para ejecutar pruebas de humo (smoke tests)
run_smoke_tests() {
    log "Ejecutando pruebas de humo..."
    
    local smoke_output="$LOGS_DIR/smoke_tests_$TIMESTAMP.log"
    
    # Pruebas de humo básicas
    {
        echo "=== SMOKE TESTS ==="
        echo "Timestamp: $(date)"
        echo ""
        
        # Test 1: Health check
        echo "1. Health Check:"
        if curl -s "http://localhost:8080/actuator/health" | grep -q "UP"; then
            echo "   ✅ PASS - Health endpoint responde"
        else
            echo "   ❌ FAIL - Health endpoint no responde"
        fi
        
        # Test 2: Swagger documentation
        echo "2. Swagger Documentation:"
        if curl -s "http://localhost:8080/swagger-ui/index.html" | grep -q "swagger"; then
            echo "   ✅ PASS - Swagger UI disponible"
        else
            echo "   ❌ FAIL - Swagger UI no disponible"
        fi
        
        # Test 3: Database connection
        echo "3. Database Connection:"
        if ./gradlew test -Dtest="**/ProductoSimpleSteps" 2>/dev/null | grep -q "SUCCESS"; then
            echo "   ✅ PASS - Base de datos conectada"
        else
            echo "   ❌ FAIL - Problemas con base de datos"
        fi
        
        echo ""
        echo "=== FIN SMOKE TESTS ==="
        
    } > "$smoke_output"
    
    cat "$smoke_output"
    success "Pruebas de humo completadas"
}

# Función para analizar resultados
analyze_results() {
    log "Analizando resultados de regresión..."
    
    local backend_status="UNKNOWN"
    local frontend_status="UNKNOWN"
    local overall_status="UNKNOWN"
    
    # Analizar logs de backend
    if [ -f "$LOGS_DIR/backend_regression_$TIMESTAMP.log" ]; then
        if grep -q "BUILD SUCCESSFUL" "$LOGS_DIR/backend_regression_$TIMESTAMP.log"; then
            backend_status="PASS"
        else
            backend_status="FAIL"
        fi
    fi
    
    # Analizar logs de frontend
    if [ -f "$LOGS_DIR/frontend_regression_$TIMESTAMP.log" ]; then
        if grep -q "passed" "$LOGS_DIR/frontend_regression_$TIMESTAMP.log" && \
           ! grep -q "failed" "$LOGS_DIR/frontend_regression_$TIMESTAMP.log"; then
            frontend_status="PASS"
        else
            frontend_status="FAIL"
        fi
    fi
    
    # Determinar estado general
    if [ "$backend_status" = "PASS" ] && [ "$frontend_status" = "PASS" ]; then
        overall_status="PASS"
    elif [ "$backend_status" = "FAIL" ] || [ "$frontend_status" = "FAIL" ]; then
        overall_status="FAIL"
    else
        overall_status="PARTIAL"
    fi
    
    # Crear reporte de resumen
    local summary_file="$REPORTS_DIR/regression_summary_$TIMESTAMP.html"
    
    cat > "$summary_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Reporte de Regresión - $(date)</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f4f4f4; padding: 20px; border-radius: 5px; }
        .status-pass { color: green; font-weight: bold; }
        .status-fail { color: red; font-weight: bold; }
        .status-partial { color: orange; font-weight: bold; }
        .test-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
        table { width: 100%; border-collapse: collapse; margin: 10px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .metric { background: #f9f9f9; padding: 10px; margin: 5px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔄 Reporte de Pruebas de Regresión</h1>
        <p><strong>Fecha:</strong> $(date)</p>
        <p><strong>Estado General:</strong> <span class="status-$overall_status">$overall_status</span></p>
    </div>
    
    <div class="test-section">
        <h2>📊 Resumen de Resultados</h2>
        <table>
            <tr>
                <th>Componente</th>
                <th>Estado</th>
                <th>Detalles</th>
                <th>Logs</th>
            </tr>
            <tr>
                <td>Backend (Cucumber + JUnit)</td>
                <td class="status-$backend_status">$backend_status</td>
                <td><a href="current/backend-cucumber/Cucumber.html">Ver Cucumber</a> | <a href="current/backend-junit/">Ver JUnit</a></td>
                <td><a href="logs/backend_regression_$TIMESTAMP.log">Ver Log</a></td>
            </tr>
            <tr>
                <td>Frontend (Playwright)</td>
                <td class="status-$frontend_status">$frontend_status</td>
                <td><a href="current/frontend-playwright/index.html">Ver Playwright</a></td>
                <td><a href="logs/frontend_regression_$TIMESTAMP.log">Ver Log</a></td>
            </tr>
        </table>
    </div>
    
    <div class="test-section">
        <h2>📋 Criterios de Regresión</h2>
        <div class="metric">
            <strong>✅ Funcionalidades Críticas:</strong> Login, CRUD Productos, Búsquedas
        </div>
        <div class="metric">
            <strong>🔒 Seguridad:</strong> Autenticación JWT, Autorización de endpoints
        </div>
        <div class="metric">
            <strong>📡 API:</strong> Endpoints principales responden correctamente
        </div>
        <div class="metric">
            <strong>🎨 UI:</strong> Flujos principales del usuario funcionan
        </div>
    </div>
    
    <div class="test-section">
        <h2>🔧 Acciones Recomendadas</h2>
        <ul>
EOF

    if [ "$overall_status" = "PASS" ]; then
        echo "            <li style='color: green;'>✅ Todas las pruebas pasaron - ¡Listo para deploy!</li>" >> "$summary_file"
    elif [ "$overall_status" = "FAIL" ]; then
        echo "            <li style='color: red;'>❌ Hay fallos críticos - NO hacer deploy</li>" >> "$summary_file"
        echo "            <li>Revisar logs detallados de fallos</li>" >> "$summary_file"
        echo "            <li>Ejecutar pruebas individualmente para diagnóstico</li>" >> "$summary_file"
    else
        echo "            <li style='color: orange;'>⚠️ Resultados parciales - Revisar antes de deploy</li>" >> "$summary_file"
    fi

    cat >> "$summary_file" << EOF
            <li>Archivar este reporte para histórico</li>
            <li>Notificar al equipo sobre resultados</li>
        </ul>
    </div>
    
    <div class="test-section">
        <h2>📁 Archivos Generados</h2>
        <ul>
            <li><strong>Smoke Tests:</strong> <a href="logs/smoke_tests_$TIMESTAMP.log">smoke_tests_$TIMESTAMP.log</a></li>
            <li><strong>Screenshots:</strong> <a href="screenshots/">Directorio screenshots/</a></li>
            <li><strong>Backups:</strong> <a href="backup/">Directorio backup/</a></li>
        </ul>
    </div>
    
</body>
</html>
EOF

    success "Reporte de resumen generado: $summary_file"
    
    # Mostrar resumen en consola
    echo ""
    echo "================================="
    echo "📋 RESUMEN DE REGRESIÓN"
    echo "================================="
    echo "Backend:  $backend_status"
    echo "Frontend: $frontend_status"
    echo "General:  $overall_status"
    echo "Reporte:  $summary_file"
    echo "================================="
    
    # Return code basado en el resultado
    case $overall_status in
        "PASS")
            return 0
            ;;
        "FAIL")
            return 1
            ;;
        *)
            return 2
            ;;
    esac
}

# Función para limpiar recursos
cleanup() {
    log "Limpiando recursos temporales..."
    
    # Mover reportes actuales a previous
    if [ -d "$REPORTS_DIR/current" ]; then
        rm -rf "$REPORTS_DIR/previous"
        mv "$REPORTS_DIR/current" "$REPORTS_DIR/previous"
    fi
    
    # Limpiar logs antiguos (mantener últimos 5)
    find "$LOGS_DIR" -name "*.log" -type f -mtime +5 -delete 2>/dev/null || true
    
    success "Limpieza completada"
}

# Función principal
main() {
    echo "========================================"
    echo "🔄 PRUEBAS DE REGRESIÓN - INVENTARIO"
    echo "========================================"
    
    local start_time=$(date +%s)
    local overall_result=0
    
    # Configurar trap para cleanup en caso de interrupción
    trap cleanup EXIT
    
    # Ejecutar pasos
    setup_environment || exit 1
    
    if ! check_system_health; then
        error "Sistema no está saludable - abortando"
        exit 1
    fi
    
    run_smoke_tests
    
    # Ejecutar regresión backend
    if ! run_backend_regression; then
        overall_result=1
    fi
    
    # Ejecutar regresión frontend
    if ! run_frontend_regression; then
        overall_result=1
    fi
    
    # Analizar y reportar
    analyze_results
    local analysis_result=$?
    
    # El resultado final es el peor de ambos
    if [ $analysis_result -gt $overall_result ]; then
        overall_result=$analysis_result
    fi
    
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    echo ""
    if [ $overall_result -eq 0 ]; then
        success "🎉 REGRESIÓN COMPLETADA EXITOSAMENTE"
        success "Duración: ${duration}s"
        success "¡Sistema listo para deploy!"
    else
        error "❌ REGRESIÓN FALLÓ"
        error "Duración: ${duration}s"
        error "¡NO hacer deploy hasta resolver fallos!"
    fi
    
    return $overall_result
}

# Ejecutar función principal
main "$@"
