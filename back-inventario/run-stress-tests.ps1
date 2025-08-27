# =============================================================================
# Script de Ejecución de Pruebas de Estrés con JMeter (PowerShell)
# Sistema de Gestión de Inventarios
# =============================================================================

param(
   [string]$Host = "localhost",
   [string]$Port = "8080",
   [int]$Threads = 50,
   [int]$Duration = 300,
   [string]$JMeterPath = "C:\jmeter\bin\jmeter.bat",
   [switch]$Help
)

# Variables de configuración
$TestPlan = "src\test\jmeter\stress-test-plan.jmx"
$ResultsDir = "target\jmeter-results"
$ReportsDir = "target\jmeter-reports"

# Función para mostrar ayuda
function Show-Help {
   Write-Host @"
Uso: .\run-stress-tests.ps1 [options]

Options:
  -Host HOST         Hostname del servidor (default: localhost)
  -Port PORT         Puerto del servidor (default: 8080)  
  -Threads NUM       Número base de usuarios (default: 50)
  -Duration SEC      Duración base en segundos (default: 300)
  -JMeterPath PATH   Ruta a jmeter.bat (default: C:\jmeter\bin\jmeter.bat)
  -Help             Mostrar esta ayuda

Ejemplos:
  .\run-stress-tests.ps1
  .\run-stress-tests.ps1 -Threads 100 -Duration 600
  .\run-stress-tests.ps1 -Host "192.168.1.100" -Port "8080"
"@
}

if ($Help) {
   Show-Help
   exit 0
}

# Función para log con colores
function Write-Log {
   param([string]$Message, [string]$Type = "INFO")
    
   $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
   switch ($Type) {
      "SUCCESS" { Write-Host "[$timestamp] [SUCCESS] $Message" -ForegroundColor Green }
      "WARNING" { Write-Host "[$timestamp] [WARNING] $Message" -ForegroundColor Yellow }
      "ERROR" { Write-Host "[$timestamp] [ERROR] $Message" -ForegroundColor Red }
      default { Write-Host "[$timestamp] [INFO] $Message" -ForegroundColor Blue }
   }
}

# Función para verificar prerequisitos
function Test-Prerequisites {
   Write-Log "Verificando prerequisitos..."
    
   # Verificar JMeter
   if (-not (Test-Path $JMeterPath)) {
      Write-Log "JMeter no encontrado en $JMeterPath" "ERROR"
      Write-Host "Descarga JMeter desde: https://jmeter.apache.org/download_jmeter.cgi"
      Write-Host "Y actualiza la variable JMeterPath o usa el parámetro -JMeterPath"
      exit 1
   }
    
   # Verificar Java
   try {
      $javaVersion = & java -version 2>&1
      if ($LASTEXITCODE -ne 0) {
         throw "Java no encontrado"
      }
   }
   catch {
      Write-Log "Java no está instalado o no está en el PATH" "ERROR"
      exit 1
   }
    
   # Verificar que el servidor esté corriendo
   try {
      $response = Invoke-WebRequest -Uri "http://$Host`:$Port/actuator/health" -TimeoutSec 5 -ErrorAction Stop
      if ($response.StatusCode -ne 200) {
         throw "Servidor no responde"
      }
   }
   catch {
      Write-Log "El servidor no parece estar corriendo en http://$Host`:$Port" "WARNING"
      Write-Host "Asegúrate de que el backend esté iniciado"
      $continue = Read-Host "¿Continuar de todos modos? (y/N)"
      if ($continue -ne "y" -and $continue -ne "Y") {
         exit 1
      }
   }
    
   Write-Log "Prerequisitos verificados" "SUCCESS"
}

# Función para preparar directorios
function Initialize-Directories {
   Write-Log "Preparando directorios..."
    
   $dirs = @(
      $ResultsDir,
      $ReportsDir,
      "$ResultsDir\jtl",
      "$ResultsDir\logs"
   )
    
   foreach ($dir in $dirs) {
      if (-not (Test-Path $dir)) {
         New-Item -ItemType Directory -Path $dir -Force | Out-Null
      }
   }
    
   Write-Log "Directorios preparados" "SUCCESS"
}

# Función para ejecutar JMeter
function Invoke-JMeterTest {
   param(
      [string]$TestName,
      [int]$TestThreads,
      [int]$TestDuration,
      [int]$RampUp = 30,
      [int]$Loops = 10
   )
    
   $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
   $testId = "${TestName}_$timestamp"
   $jtlFile = "$ResultsDir\jtl\$testId.jtl"
   $logFile = "$ResultsDir\logs\$testId.log"
   $reportDir = "$ReportsDir\$testId"
    
   Write-Log "Ejecutando prueba: $TestName..."
   Write-Log "Threads: $TestThreads, Duration: $TestDuration seg, RampUp: $RampUp seg"
    
   $arguments = @(
      "-n",
      "-t", $TestPlan,
      "-l", $jtlFile,
      "-j", $logFile,
      "-e",
      "-o", $reportDir,
      "-Jhost=$Host",
      "-Jport=$Port",
      "-Jthreads=$TestThreads",
      "-Jduration=$TestDuration",
      "-Jrampup=$RampUp",
      "-Jloops=$Loops"
   )
    
   try {
      & $JMeterPath $arguments
        
      if ($LASTEXITCODE -eq 0) {
         Write-Log "Prueba $TestName completada exitosamente" "SUCCESS"
         Write-Log "Resultados en: $reportDir\index.html"
         return $testId
      }
      else {
         Write-Log "Falló la prueba $TestName (Exit Code: $LASTEXITCODE)" "ERROR"
         return $null
      }
   }
   catch {
      Write-Log "Error ejecutando prueba $TestName`: $($_.Exception.Message)" "ERROR"
      return $null
   }
}

# Función para ejecutar prueba de carga normal
function Start-LoadTest {
   return Invoke-JMeterTest -TestName "load_test" -TestThreads $Threads -TestDuration $Duration -RampUp 30 -Loops 10
}

# Función para ejecutar prueba de estrés
function Start-StressTest {
   $stressThreads = $Threads * 4
   $stressDuration = $Duration * 2
   return Invoke-JMeterTest -TestName "stress_test" -TestThreads $stressThreads -TestDuration $stressDuration -RampUp 60 -Loops 5
}

# Función para ejecutar prueba de pico
function Start-SpikeTest {
   $spikeThreads = $Threads * 10
   return Invoke-JMeterTest -TestName "spike_test" -TestThreads $spikeThreads -TestDuration 60 -RampUp 5 -Loops 2
}

# Función para generar reporte consolidado
function New-ConsolidatedReport {
   param([array]$TestResults)
    
   Write-Log "Generando reporte consolidado..."
    
   $reportFile = "$ReportsDir\consolidated_report.html"
   $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
   $html = @"
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
        .metric { display: inline-block; margin: 10px; padding: 10px; background: #f9f9f9; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔥 Reporte de Pruebas de Estrés - Sistema de Inventarios</h1>
        <p><strong>Fecha:</strong> $timestamp</p>
        <p><strong>Servidor:</strong> $Host`:$Port</p>
        <p><strong>Configuración:</strong> Threads base: $Threads, Duración: $Duration segundos</p>
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
"@

   # Agregar filas de la tabla dinámicamente
   $testTypes = @(
      @{Name = "Carga Normal"; Users = $Threads; Duration = "$Duration seg"; Pattern = "load_test_*" },
      @{Name = "Estrés Intensivo"; Users = ($Threads * 4); Duration = "$(($Duration * 2)) seg"; Pattern = "stress_test_*" },
      @{Name = "Pico (Spike)"; Users = ($Threads * 10); Duration = "60 seg"; Pattern = "spike_test_*" }
   )
    
   foreach ($test in $testTypes) {
      $status = if ($TestResults -and ($TestResults | Where-Object { $_ -like "*$($test.Pattern.Replace('*',''))*" })) {
         '<span class="success">✅ Completada</span>'
      }
      else {
         '<span class="error">❌ No ejecutada</span>'
      }
        
      $html += @"
            <tr>
                <td>$($test.Name)</td>
                <td>$($test.Users)</td>
                <td>$($test.Duration)</td>
                <td>$status</td>
                <td><a href="./$($test.Pattern)/index.html">Ver Reporte</a></td>
            </tr>
"@
   }
    
   $html += @"
        </table>
    </div>
    
    <div class="test-section">
        <h2>🎯 Criterios de Evaluación</h2>
        <div class="metric">
            <strong>⏱️ Tiempo de Respuesta</strong><br>
            Promedio &lt; 500ms<br>
            95% &lt; 1000ms
        </div>
        <div class="metric">
            <strong>🚀 Throughput</strong><br>
            &gt; 100 req/seg<br>
            bajo carga normal
        </div>
        <div class="metric">
            <strong>❌ Tasa de Error</strong><br>
            &lt; 1% en pruebas<br>
            de carga
        </div>
        <div class="metric">
            <strong>🔄 Estabilidad</strong><br>
            Sin degradación<br>
            durante estrés
        </div>
    </div>
    
    <div class="test-section">
        <h2>📝 Recomendaciones</h2>
        <ul>
            <li>✅ Revisar logs de aplicación para identificar cuellos de botella</li>
            <li>📊 Monitorear uso de CPU y memoria durante las pruebas</li>
            <li>🗄️ Considerar optimización de queries de base de datos</li>
            <li>⚡ Evaluar implementación de cache para consultas frecuentes</li>
            <li>🔌 Configurar connection pooling apropiado</li>
            <li>📈 Implementar métricas de monitoreo en producción</li>
        </ul>
    </div>
    
    <div class="test-section">
        <h2>🔧 Comandos Útiles</h2>
        <p><strong>Ver logs de JMeter:</strong></p>
        <code>Get-Content "$ResultsDir\logs\*.log" | Select-String "ERROR"</code>
        
        <p><strong>Analizar archivos JTL:</strong></p>
        <code>Import-Csv "$ResultsDir\jtl\*.jtl" | Measure-Object -Property elapsed -Average</code>
    </div>
    
</body>
</html>
"@
    
   $html | Out-File -FilePath $reportFile -Encoding UTF8
   Write-Log "Reporte consolidado generado: $reportFile" "SUCCESS"
}

# Función para mostrar menú
function Show-Menu {
   Write-Host ""
   Write-Host "======================================"
   Write-Host "🚀 PRUEBAS DE ESTRÉS - INVENTARIO API"
   Write-Host "======================================"
   Write-Host ""
   Write-Host "Selecciona el tipo de prueba:"
   Write-Host "1) Prueba de carga normal ($Threads usuarios, $Duration seg)"
   Write-Host "2) Prueba de estrés intensiva ($(($Threads * 4)) usuarios, $(($Duration * 2)) seg)"
   Write-Host "3) Prueba de pico ($(($Threads * 10)) usuarios, 60 seg)"
   Write-Host "4) Ejecutar todas las pruebas"
   Write-Host "5) Solo generar reporte"
   Write-Host "0) Salir"
   Write-Host ""
}

# Función principal
function Main {
   try {
      Test-Prerequisites
      Initialize-Directories
        
      Show-Menu
        
      do {
         $option = Read-Host "Opción (0-5)"
         $testResults = @()
            
         switch ($option) {
            "1" {
               $result = Start-LoadTest
               if ($result) { $testResults += $result }
            }
            "2" {
               $result = Start-StressTest
               if ($result) { $testResults += $result }
            }
            "3" {
               $result = Start-SpikeTest
               if ($result) { $testResults += $result }
            }
            "4" {
               Write-Log "Ejecutando suite completa de pruebas..."
                    
               $result1 = Start-LoadTest
               if ($result1) { 
                  $testResults += $result1
                  Write-Log "Esperando 30 segundos antes de la siguiente prueba..."
                  Start-Sleep -Seconds 30
               }
                    
               $result2 = Start-StressTest
               if ($result2) { 
                  $testResults += $result2
                  Write-Log "Esperando 30 segundos antes de la siguiente prueba..."
                  Start-Sleep -Seconds 30
               }
                    
               $result3 = Start-SpikeTest
               if ($result3) { $testResults += $result3 }
                    
               New-ConsolidatedReport -TestResults $testResults
            }
            "5" {
               New-ConsolidatedReport -TestResults @()
            }
            "0" {
               Write-Log "Saliendo..." "INFO"
               break
            }
            default {
               Write-Log "Opción inválida" "ERROR"
            }
         }
            
         if ($option -ne "0") {
            Write-Host ""
            Write-Host "Presiona Enter para continuar..."
            Read-Host
            Show-Menu
         }
            
      } while ($option -ne "0")
        
      Write-Log "🎉 Proceso completado" "SUCCESS"
      Write-Log "Resultados disponibles en: $ReportsDir"
        
   }
   catch {
      Write-Log "Error inesperado: $($_.Exception.Message)" "ERROR"
      exit 1
   }
}

# Ejecutar función principal
Main
