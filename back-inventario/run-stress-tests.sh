#!/bin/bash

# =============================================================================
# Script de Ejecución de Pruebas de Estrés con JMeter
# Sistema de Gestión de Inventarios
# =============================================================================

# Variables de configuración
JMETER_HOME="${JMETER_HOME:-/opt/jmeter}"
TEST_PLAN="src/test/jmeter/stress-test-plan.jmx"
RESULTS_DIR="target/jmeter-results"
REPORTS_DIR="target/jmeter-reports"
HOST="${HOST:-localhost}"
PORT="${PORT:-8080}"
THREADS="${THREADS:-50}"
DURATION="${DURATION:-300}"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# Función para verificar prerequisitos
check_prerequisites() {
    log "Verificando prerequisitos..."
    
    # Verificar JMeter
    if [ ! -d "$JMETER_HOME" ]; then
        error "JMeter no encontrado en $JMETER_HOME"
        echo "Descarga JMeter desde: https://jmeter.apache.org/download_jmeter.cgi"
        exit 1
    fi
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        error "Java no está instalado"
        exit 1
    fi
    
    # Verificar que el servidor esté corriendo
    if ! curl -s "http://$HOST:$PORT/actuator/health" > /dev/null; then
        warning "El servidor no parece estar corriendo en http://$HOST:$PORT"
        echo "Asegúrate de que el backend esté iniciado"
        read -p "¿Continuar de todos modos? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
    
    success "Prerequisitos verificados"
}

# Función para preparar directorios
prepare_directories() {
    log "Preparando directorios..."
    
    mkdir -p "$RESULTS_DIR"
    mkdir -p "$REPORTS_DIR"
    mkdir -p "$RESULTS_DIR/jtl"
    mkdir -p "$RESULTS_DIR/logs"
    
    success "Directorios preparados"
}

# Función para ejecutar prueba de carga normal
run_load_test() {
    log "Ejecutando prueba de carga normal..."
    
    local test_name="load_test_$(date +%Y%m%d_%H%M%S)"
    local jtl_file="$RESULTS_DIR/jtl/${test_name}.jtl"
    local log_file="$RESULTS_DIR/logs/${test_name}.log"
    
    $JMETER_HOME/bin/jmeter \
        -n \
        -t "$TEST_PLAN" \
        -l "$jtl_file" \
        -j "$log_file" \
        -e \
        -o "$REPORTS_DIR/${test_name}" \
        -Jhost="$HOST" \
        -Jport="$PORT" \
        -Jthreads="$THREADS" \
        -Jduration="$DURATION" \
        -Jrampup=30 \
        -Jloops=10
    
    if [ $? -eq 0 ]; then
        success "Prueba de carga completada exitosamente"
        log "Resultados en: $REPORTS_DIR/${test_name}/index.html"
    else
        error "Falló la prueba de carga"
        return 1
    fi
}

# Función para ejecutar prueba de estrés
run_stress_test() {
    log "Ejecutando prueba de estrés intensiva..."
    
    local test_name="stress_test_$(date +%Y%m%d_%H%M%S)"
    local jtl_file="$RESULTS_DIR/jtl/${test_name}.jtl"
    local log_file="$RESULTS_DIR/logs/${test_name}.log"
    local stress_threads=$((THREADS * 4))  # 4x usuarios para estrés
    
    $JMETER_HOME/bin/jmeter \
        -n \
        -t "$TEST_PLAN" \
        -l "$jtl_file" \
        -j "$log_file" \
        -e \
        -o "$REPORTS_DIR/${test_name}" \
        -Jhost="$HOST" \
        -Jport="$PORT" \
        -Jthreads="$stress_threads" \
        -Jduration="$((DURATION * 2))" \
        -Jrampup=60 \
        -Jloops=5
    
    if [ $? -eq 0 ]; then
        success "Prueba de estrés completada"
        log "Resultados en: $REPORTS_DIR/${test_name}/index.html"
    else
        error "Falló la prueba de estrés"
        return 1
    fi
}

# Función para ejecutar prueba de pico
run_spike_test() {
    log "Ejecutando prueba de pico (spike test)..."
    
    local test_name="spike_test_$(date +%Y%m%d_%H%M%S)"
    local jtl_file="$RESULTS_DIR/jtl/${test_name}.jtl"
    local log_file="$RESULTS_DIR/logs/${test_name}.log"
    local spike_threads=$((THREADS * 10))  # 10x usuarios para pico
    
    $JMETER_HOME/bin/jmeter \
        -n \
        -t "$TEST_PLAN" \
        -l "$jtl_file" \
        -j "$log_file" \
        -e \
        -o "$REPORTS_DIR/${test_name}" \
        -Jhost="$HOST" \
        -Jport="$PORT" \
        -Jthreads="$spike_threads" \
        -Jduration="60" \
        -Jrampup="5" \
        -Jloops="2"
    
    if [ $? -eq 0 ]; then
        success "Prueba de pico completada"
        log "Resultados en: $REPORTS_DIR/${test_name}/index.html"
    else
        error "Falló la prueba de pico"
        return 1
    fi
}

# Función para generar reporte consolidado
generate_consolidated_report() {
    log "Generando reporte consolidado..."
    
    local report_file="$REPORTS_DIR/consolidated_report.html"
    
    cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Reporte Consolidado de Pruebas de Estrés</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f4f4f4; padding: 20px; border-radius: 5px; }
        .test-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
        .success { color: green; }
        .warning { color: orange; }
        .error { color: red; }
        table { width: 100%; border-collapse: collapse; margin: 10px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔥 Reporte de Pruebas de Estrés - Sistema de Inventarios</h1>
        <p><strong>Fecha:</strong> $(date)</p>
        <p><strong>Servidor:</strong> $HOST:$PORT</p>
        <p><strong>Configuración:</strong> Threads base: $THREADS, Duración: $DURATION segundos</p>
    </div>
    
    <div class="test-section">
        <h2>📊 Resumen de Pruebas Ejecutadas</h2>
        <table>
            <tr>
                <th>Tipo de Prueba</th>
                <th>Usuarios</th>
                <th>Duración</th>
                <th>Estado</th>
                <th>Reporte Detallado</th>
            </tr>
            <tr>
                <td>Carga Normal</td>
                <td>$THREADS</td>
                <td>$DURATION seg</td>
                <td class="success">✅ Completada</td>
                <td><a href="./load_test_*/index.html">Ver Reporte</a></td>
            </tr>
            <tr>
                <td>Estrés Intensivo</td>
                <td>$((THREADS * 4))</td>
                <td>$((DURATION * 2)) seg</td>
                <td class="success">✅ Completada</td>
                <td><a href="./stress_test_*/index.html">Ver Reporte</a></td>
            </tr>
            <tr>
                <td>Pico (Spike)</td>
                <td>$((THREADS * 10))</td>
                <td>60 seg</td>
                <td class="success">✅ Completada</td>
                <td><a href="./spike_test_*/index.html">Ver Reporte</a></td>
            </tr>
        </table>
    </div>
    
    <div class="test-section">
        <h2>🎯 Criterios de Evaluación</h2>
        <ul>
            <li><strong>Tiempo de Respuesta:</strong> Promedio &lt; 500ms, 95% &lt; 1000ms</li>
            <li><strong>Throughput:</strong> &gt; 100 req/seg bajo carga normal</li>
            <li><strong>Tasa de Error:</strong> &lt; 1% en pruebas de carga</li>
            <li><strong>Estabilidad:</strong> Sin degradación significativa durante estrés</li>
            <li><strong>Recuperación:</strong> Sistema estable después de picos de carga</li>
        </ul>
    </div>
    
    <div class="test-section">
        <h2>📝 Recomendaciones</h2>
        <ul>
            <li>Revisar logs de aplicación para identificar cuellos de botella</li>
            <li>Monitorear uso de CPU y memoria durante las pruebas</li>
            <li>Considerar optimización de queries de base de datos</li>
            <li>Evaluar implementación de cache para consultas frecuentes</li>
            <li>Configurar connection pooling apropiado</li>
        </ul>
    </div>
    
</body>
</html>
EOF
    
    success "Reporte consolidado generado: $report_file"
}

# Función principal
main() {
    echo "======================================"
    echo "🚀 PRUEBAS DE ESTRÉS - INVENTARIO API"
    echo "======================================"
    
    check_prerequisites
    prepare_directories
    
    echo ""
    echo "Selecciona el tipo de prueba:"
    echo "1) Prueba de carga normal"
    echo "2) Prueba de estrés intensiva"
    echo "3) Prueba de pico (spike)"
    echo "4) Ejecutar todas las pruebas"
    echo "5) Solo generar reporte"
    
    read -p "Opción (1-5): " option
    
    case $option in
        1)
            run_load_test
            ;;
        2)
            run_stress_test
            ;;
        3)
            run_spike_test
            ;;
        4)
            log "Ejecutando suite completa de pruebas..."
            run_load_test && sleep 30
            run_stress_test && sleep 30
            run_spike_test
            generate_consolidated_report
            ;;
        5)
            generate_consolidated_report
            ;;
        *)
            error "Opción inválida"
            exit 1
            ;;
    esac
    
    echo ""
    success "🎉 Proceso completado"
    log "Resultados disponibles en: $REPORTS_DIR"
}

# Verificar argumentos de línea de comandos
while [[ $# -gt 0 ]]; do
    case $1 in
        --host)
            HOST="$2"
            shift 2
            ;;
        --port)
            PORT="$2"
            shift 2
            ;;
        --threads)
            THREADS="$2"
            shift 2
            ;;
        --duration)
            DURATION="$2"
            shift 2
            ;;
        --help)
            echo "Uso: $0 [options]"
            echo "Options:"
            echo "  --host HOST       Hostname del servidor (default: localhost)"
            echo "  --port PORT       Puerto del servidor (default: 8080)"
            echo "  --threads NUM     Número base de usuarios (default: 50)"
            echo "  --duration SEC    Duración base en segundos (default: 300)"
            echo "  --help           Mostrar esta ayuda"
            exit 0
            ;;
        *)
            error "Opción desconocida: $1"
            exit 1
            ;;
    esac
done

# Ejecutar función principal
main
