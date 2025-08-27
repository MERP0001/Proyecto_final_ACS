# =============================================================================
# Script de Ejecución de Pruebas de Regresión (PowerShell)
# Sistema de Gestión de Inventarios
# =============================================================================

param(
   [switch]$SkipSmoke,
   [switch]$BackendOnly,
   [switch]$FrontendOnly,
   [switch]$Verbose,
   [switch]$Help
)

# Variables de configuración
$ReportsDir = "target\regression-reports"
$ScreenshotsDir = "$ReportsDir\screenshots"
$LogsDir = "$ReportsDir\logs"
$BackupDir = "$ReportsDir\backup"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

function Show-Help {
   Write-Host @"
Script de Pruebas de Regresión - Sistema de Inventarios

Uso: .\run-regression-tests.ps1 [options]

Options:
  -SkipSmoke      Omitir pruebas de humo
  -BackendOnly    Solo ejecutar pruebas de backend
  -FrontendOnly   Solo ejecutar pruebas de frontend
  -Verbose        Mostrar output detallado
  -Help          Mostrar esta ayuda

Ejemplos:
  .\run-regression-tests.ps1
  .\run-regression-tests.ps1 -BackendOnly
  .\run-regression-tests.ps1 -Verbose
"@
}

if ($Help) {
   Show-Help
   exit 0
}

function Write-Log {
   param([string]$Message, [string]$Type = "INFO")
    
   $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
   switch ($Type) {
      "SUCCESS" { Write-Host "[$timestamp] [SUCCESS] $Message" -ForegroundColor Green }
      "WARNING" { Write-Host "[$timestamp] [WARNING] $Message" -ForegroundColor Yellow }
      "ERROR" { Write-Host "[$timestamp] [ERROR] $Message" -ForegroundColor Red }
      "INFO" { Write-Host "[$timestamp] [INFO] $Message" -ForegroundColor Blue }
      default { Write-Host "[$timestamp] [INFO] $Message" -ForegroundColor Blue }
   }
}

function Initialize-Environment {
   Write-Log "Preparando entorno para pruebas de regresión..."
    
   # Crear directorios
   $dirs = @($ReportsDir, $ScreenshotsDir, $LogsDir, $BackupDir)
   foreach ($dir in $dirs) {
      if (-not (Test-Path $dir)) {
         New-Item -ItemType Directory -Path $dir -Force | Out-Null
      }
   }
    
   # Backup de reportes anteriores
   $previousDir = "$ReportsDir\previous"
   if (Test-Path $previousDir) {
      $backupPath = "$BackupDir\regression_$Timestamp"
      Move-Item -Path $previousDir -Destination $backupPath -Force
      Write-Log "Backup de reportes anteriores realizado"
   }
    
   # Crear directorio para nueva ejecución
   $currentDir = "$ReportsDir\current"
   if (-not (Test-Path $currentDir)) {
      New-Item -ItemType Directory -Path $currentDir -Force | Out-Null
   }
    
   Write-Log "Entorno preparado" "SUCCESS"
}

function Test-SystemHealth {
   Write-Log "Verificando estado del sistema..."
    
   try {
      # Verificar que la aplicación compile
      $buildResult = & .\gradlew.bat build -x test 2>&1
      if ($LASTEXITCODE -ne 0) {
         Write-Log "La aplicación no compila correctamente" "ERROR"
         return $false
      }
        
      # Verificar endpoints básicos si está disponible curl
      if (Get-Command curl -ErrorAction SilentlyContinue) {
         try {
            $response = & curl -s "http://localhost:8080/actuator/health" 2>$null
            if ($response -match "UP") {
               Write-Log "Sistema saludable" "SUCCESS"
            }
            else {
               Write-Log "Health endpoint no responde - continuando" "WARNING"
            }
         }
         catch {
            Write-Log "No se pudo verificar health endpoint" "WARNING"
         }
      }
        
      return $true
   }
   catch {
      Write-Log "Error verificando estado del sistema: $($_.Exception.Message)" "ERROR"
      return $false
   }
}

function Invoke-SmokeTests {
   Write-Log "Ejecutando pruebas de humo..."
    
   $smokeOutput = "$LogsDir\smoke_tests_$Timestamp.log"
   $smokeResults = @()
    
   # Test 1: Compilación
   Write-Log "  - Test de compilación..."
   try {
      $buildResult = & .\gradlew.bat compileJava 2>&1
      if ($LASTEXITCODE -eq 0) {
         $smokeResults += "✅ PASS - Compilación exitosa"
      }
      else {
         $smokeResults += "❌ FAIL - Error de compilación"
      }
   }
   catch {
      $smokeResults += "❌ FAIL - Error ejecutando compilación"
   }
    
   # Test 2: Pruebas unitarias básicas
   Write-Log "  - Test de pruebas unitarias básicas..."
   try {
      $testResult = & .\gradlew.bat test --tests "*ProductoSimpleSteps" 2>&1
      if ($LASTEXITCODE -eq 0) {
         $smokeResults += "✅ PASS - Pruebas unitarias básicas"
      }
      else {
         $smokeResults += "❌ FAIL - Fallan pruebas unitarias básicas"
      }
   }
   catch {
      $smokeResults += "❌ FAIL - Error ejecutando pruebas unitarias"
   }
    
   # Test 3: Estructura de archivos
   Write-Log "  - Test de estructura de archivos..."
   $criticalFiles = @(
      "src\main\java\org\example\proyectofinal\ProyectoFinalApplication.java",
      "src\main\resources\application.properties",
      "build.gradle"
   )
    
   $filesOk = $true
   foreach ($file in $criticalFiles) {
      if (-not (Test-Path $file)) {
         $filesOk = $false
         break
      }
   }
    
   if ($filesOk) {
      $smokeResults += "✅ PASS - Estructura de archivos correcta"
   }
   else {
      $smokeResults += "❌ FAIL - Faltan archivos críticos"
   }
    
   # Escribir resultados
   $smokeContent = @"
=== SMOKE TESTS ===
Timestamp: $(Get-Date)

$($smokeResults -join "`n")

=== FIN SMOKE TESTS ===
"@
    
   $smokeContent | Out-File -FilePath $smokeOutput -Encoding UTF8
    
   # Mostrar resultados
   Write-Host ""
   Write-Host "📋 Resultados de Smoke Tests:"
   foreach ($result in $smokeResults) {
      if ($result -match "✅") {
         Write-Host "  $result" -ForegroundColor Green
      }
      else {
         Write-Host "  $result" -ForegroundColor Red
      }
   }
   Write-Host ""
    
   Write-Log "Pruebas de humo completadas" "SUCCESS"
    
   # Retornar si alguna falló
   return -not ($smokeResults -match "❌")
}

function Invoke-BackendRegression {
   Write-Log "Ejecutando pruebas de regresión del backend..."
    
   $testOutput = "$LogsDir\backend_regression_$Timestamp.log"
    
   try {
      # Ejecutar todas las pruebas de Cucumber/JUnit
      $arguments = @(
         "test",
         "--tests", "*RegressionTestSuite",
         "--info"
      )
        
      if ($Verbose) {
         $arguments += "--debug"
      }
        
      $testResult = & .\gradlew.bat $arguments 2>&1
      $testResult | Out-File -FilePath $testOutput -Encoding UTF8
        
      if ($LASTEXITCODE -eq 0) {
         Write-Log "Pruebas de regresión backend PASARON ✅" "SUCCESS"
            
         # Copiar reportes
         if (Test-Path "build\reports\tests") {
            Copy-Item -Path "build\reports\tests" -Destination "$ReportsDir\current\backend-junit" -Recurse -Force
         }
            
         if (Test-Path "target\cucumber-reports") {
            Copy-Item -Path "target\cucumber-reports" -Destination "$ReportsDir\current\backend-cucumber" -Recurse -Force
         }
            
         return $true
      }
      else {
         Write-Log "Pruebas de regresión backend FALLARON ❌" "ERROR"
         Write-Log "Ver detalles en: $testOutput" "WARNING"
         return $false
      }
   }
   catch {
      Write-Log "Error ejecutando pruebas backend: $($_.Exception.Message)" "ERROR"
      return $false
   }
}

function Invoke-FrontendRegression {
   Write-Log "Ejecutando pruebas de regresión del frontend..."
    
   if (-not (Test-Path "front-inventario")) {
      Write-Log "Directorio front-inventario no encontrado" "ERROR"
      return $false
   }
    
   $originalLocation = Get-Location
   $testOutput = "..\$LogsDir\frontend_regression_$Timestamp.log"
    
   try {
      Set-Location "front-inventario"
        
      # Verificar que npm esté disponible
      if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
         Write-Log "npm no está disponible" "ERROR"
         return $false
      }
        
      # Ejecutar pruebas críticas de Playwright
      $testArgs = @("test")
      if (-not $Verbose) {
         $testArgs += "--reporter=line"
      }
        
      $testResult = & npm $testArgs 2>&1
      $testResult | Out-File -FilePath $testOutput -Encoding UTF8
        
      if ($LASTEXITCODE -eq 0) {
         Write-Log "Pruebas de regresión frontend PASARON ✅" "SUCCESS"
            
         # Copiar reportes
         if (Test-Path "playwright-report") {
            Copy-Item -Path "playwright-report" -Destination "..\$ReportsDir\current\frontend-playwright" -Recurse -Force
         }
            
         if (Test-Path "test-results") {
            Copy-Item -Path "test-results" -Destination "..\$ReportsDir\current\frontend-results" -Recurse -Force
         }
            
         return $true
      }
      else {
         Write-Log "Pruebas de regresión frontend FALLARON ❌" "ERROR"
         Write-Log "Ver detalles en: $testOutput" "WARNING"
         return $false
      }
   }
   catch {
      Write-Log "Error ejecutando pruebas frontend: $($_.Exception.Message)" "ERROR"
      return $false
   }
   finally {
      Set-Location $originalLocation
   }
}

function New-AnalysisReport {
   param(
      [bool]$BackendResult,
      [bool]$FrontendResult,
      [bool]$SmokeResult
   )
    
   Write-Log "Analizando resultados de regresión..."
    
   # Determinar estado general
   $backendStatus = if ($BackendResult) { "PASS" } else { "FAIL" }
   $frontendStatus = if ($FrontendResult) { "PASS" } else { "FAIL" }
   $smokeStatus = if ($SmokeResult) { "PASS" } else { "FAIL" }
    
   $overallStatus = "FAIL"
   if ($BackendResult -and $FrontendResult -and $SmokeResult) {
      $overallStatus = "PASS"
   }
   elseif ($BackendResult -or $FrontendResult) {
      $overallStatus = "PARTIAL"
   }
    
   # Crear reporte HTML
   $summaryFile = "$ReportsDir\regression_summary_$Timestamp.html"
   $currentDate = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
   $htmlContent = @"
<!DOCTYPE html>
<html>
<head>
    <title>Reporte de Regresión - $currentDate</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
        .status-pass { color: #28a745; font-weight: bold; }
        .status-fail { color: #dc3545; font-weight: bold; }
        .status-partial { color: #ffc107; font-weight: bold; }
        .test-section { margin: 20px 0; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background: #fafafa; }
        table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f8f9fa; font-weight: 600; }
        .metric { background: #e3f2fd; padding: 15px; margin: 10px 0; border-radius: 8px; border-left: 4px solid #2196f3; }
        .recommendations { background: #fff3cd; padding: 15px; border-radius: 8px; border-left: 4px solid #ffc107; }
        .success-box { background: #d4edda; padding: 15px; border-radius: 8px; border-left: 4px solid #28a745; }
        .error-box { background: #f8d7da; padding: 15px; border-radius: 8px; border-left: 4px solid #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔄 Reporte de Pruebas de Regresión</h1>
            <p><strong>Fecha:</strong> $currentDate</p>
            <p><strong>Estado General:</strong> <span class="status-$($overallStatus.ToLower())">$overallStatus</span></p>
        </div>
        
        <div class="test-section">
            <h2>📊 Resumen Ejecutivo</h2>
"@

   if ($overallStatus -eq "PASS") {
      $htmlContent += '<div class="success-box">✅ <strong>ÉXITO:</strong> Todas las pruebas de regresión pasaron. El sistema está listo para deploy.</div>'
   }
   elseif ($overallStatus -eq "FAIL") {
      $htmlContent += '<div class="error-box">❌ <strong>FALLO:</strong> Hay fallos críticos en las pruebas. NO proceder con deploy.</div>'
   }
   else {
      $htmlContent += '<div class="recommendations">⚠️ <strong>PARCIAL:</strong> Algunos componentes fallaron. Revisar antes de proceder.</div>'
   }

   $htmlContent += @"
        </div>
        
        <div class="test-section">
            <h2>📋 Detalle de Resultados</h2>
            <table>
                <tr>
                    <th>Componente</th>
                    <th>Estado</th>
                    <th>Ejecutado</th>
                    <th>Reportes</th>
                    <th>Logs</th>
                </tr>
                <tr>
                    <td>🔥 Smoke Tests</td>
                    <td class="status-$($smokeStatus.ToLower())">$smokeStatus</td>
                    <td>$(if (-not $SkipSmoke) {'✅'} else {'❌ Omitido'})</td>
                    <td>N/A</td>
                    <td><a href="logs/smoke_tests_$Timestamp.log">Ver Log</a></td>
                </tr>
                <tr>
                    <td>⚙️ Backend (Spring Boot)</td>
                    <td class="status-$($backendStatus.ToLower())">$backendStatus</td>
                    <td>$(if (-not $FrontendOnly) {'✅'} else {'❌ Omitido'})</td>
                    <td><a href="current/backend-cucumber/">Cucumber</a> | <a href="current/backend-junit/">JUnit</a></td>
                    <td><a href="logs/backend_regression_$Timestamp.log">Ver Log</a></td>
                </tr>
                <tr>
                    <td>🎨 Frontend (Next.js)</td>
                    <td class="status-$($frontendStatus.ToLower())">$frontendStatus</td>
                    <td>$(if (-not $BackendOnly) {'✅'} else {'❌ Omitido'})</td>
                    <td><a href="current/frontend-playwright/">Playwright</a></td>
                    <td><a href="logs/frontend_regression_$Timestamp.log">Ver Log</a></td>
                </tr>
            </table>
        </div>
        
        <div class="test-section">
            <h2>🎯 Cobertura de Pruebas</h2>
            <div class="metric">
                <strong>🔐 Autenticación:</strong> Login, JWT, Autorización
            </div>
            <div class="metric">
                <strong>📦 CRUD Productos:</strong> Crear, Leer, Actualizar, Eliminar
            </div>
            <div class="metric">
                <strong>🔍 Búsquedas:</strong> Filtros, Paginación, Ordenamiento
            </div>
            <div class="metric">
                <strong>📂 Categorías:</strong> Gestión completa de categorías
            </div>
            <div class="metric">
                <strong>🌐 API:</strong> Endpoints críticos, Validaciones, Errores
            </div>
            <div class="metric">
                <strong>🖥️ UI:</strong> Flujos de usuario, Navegación, Responsividad
            </div>
        </div>
        
        <div class="test-section">
            <h2>🚀 Próximos Pasos</h2>
"@

   if ($overallStatus -eq "PASS") {
      $htmlContent += @"
            <div class="success-box">
                <h4>✅ Sistema Aprobado para Deploy</h4>
                <ul>
                    <li>Notificar al equipo del éxito de regresión</li>
                    <li>Proceder con deployment a staging/producción</li>
                    <li>Archivar este reporte para auditoría</li>
                    <li>Continuar con pruebas de aceptación usuario final</li>
                </ul>
            </div>
"@
   }
   else {
      $htmlContent += @"
            <div class="error-box">
                <h4>❌ Acción Requerida</h4>
                <ul>
                    <li>Revisar logs detallados de componentes fallidos</li>
                    <li>Ejecutar pruebas individuales para diagnóstico</li>
                    <li>Corregir fallos identificados</li>
                    <li>Re-ejecutar regresión antes de deploy</li>
                    <li>Notificar al equipo sobre fallos encontrados</li>
                </ul>
            </div>
"@
   }

   $htmlContent += @"
        </div>
        
        <div class="test-section">
            <h2>📁 Archivos y Herramientas</h2>
            <h4>Comandos Útiles:</h4>
            <code>.\gradlew.bat test --tests "*RegressionTestSuite"</code><br>
            <code>cd front-inventario && npm test</code><br>
            <code>Get-Content "$LogsDir\*.log" | Select-String "ERROR"</code>
            
            <h4>Ubicaciones:</h4>
            <ul>
                <li><strong>Reportes:</strong> $ReportsDir\current\</li>
                <li><strong>Logs:</strong> $LogsDir\</li>
                <li><strong>Screenshots:</strong> $ScreenshotsDir\</li>
                <li><strong>Backups:</strong> $BackupDir\</li>
            </ul>
        </div>
        
        <div class="test-section">
            <h2>📊 Métricas de Ejecución</h2>
            <div class="metric">
                <strong>⏱️ Timestamp:</strong> $Timestamp
            </div>
            <div class="metric">
                <strong>💻 Sistema:</strong> Windows PowerShell
            </div>
            <div class="metric">
                <strong>🔧 Herramientas:</strong> Gradle, npm, Cucumber, Playwright
            </div>
        </div>
    </div>
</body>
</html>
"@

   $htmlContent | Out-File -FilePath $summaryFile -Encoding UTF8
    
   Write-Log "Reporte de análisis generado: $summaryFile" "SUCCESS"
    
   # Mostrar resumen en consola
   Write-Host ""
   Write-Host "==========================================" -ForegroundColor Cyan
   Write-Host "📋 RESUMEN DE REGRESIÓN" -ForegroundColor Cyan
   Write-Host "==========================================" -ForegroundColor Cyan
   Write-Host "Smoke Tests: " -NoNewline
   if ($SmokeResult) { Write-Host $smokeStatus -ForegroundColor Green } else { Write-Host $smokeStatus -ForegroundColor Red }
   Write-Host "Backend:     " -NoNewline
   if ($BackendResult) { Write-Host $backendStatus -ForegroundColor Green } else { Write-Host $backendStatus -ForegroundColor Red }
   Write-Host "Frontend:    " -NoNewline  
   if ($FrontendResult) { Write-Host $frontendStatus -ForegroundColor Green } else { Write-Host $frontendStatus -ForegroundColor Red }
   Write-Host "General:     " -NoNewline
   switch ($overallStatus) {
      "PASS" { Write-Host $overallStatus -ForegroundColor Green }
      "FAIL" { Write-Host $overallStatus -ForegroundColor Red }
      default { Write-Host $overallStatus -ForegroundColor Yellow }
   }
   Write-Host "Reporte:     $summaryFile" -ForegroundColor Blue
   Write-Host "==========================================" -ForegroundColor Cyan
    
   return $overallStatus
}

function Remove-TempResources {
   Write-Log "Limpiando recursos temporales..."
    
   # Mover reportes actuales a previous
   $currentDir = "$ReportsDir\current"
   $previousDir = "$ReportsDir\previous"
    
   if (Test-Path $currentDir) {
      if (Test-Path $previousDir) {
         Remove-Item -Path $previousDir -Recurse -Force
      }
      Move-Item -Path $currentDir -Destination $previousDir -Force
   }
    
   # Limpiar logs antiguos (mantener últimos 10)
   $logFiles = Get-ChildItem -Path $LogsDir -Filter "*.log" | Sort-Object LastWriteTime -Descending
   if ($logFiles.Count -gt 10) {
      $logFiles | Select-Object -Skip 10 | Remove-Item -Force
   }
    
   Write-Log "Limpieza completada" "SUCCESS"
}

# Función principal
function Main {
   $startTime = Get-Date
   $overallResult = $true
    
   try {
      Write-Host "========================================" -ForegroundColor Cyan
      Write-Host "🔄 PRUEBAS DE REGRESIÓN - INVENTARIO" -ForegroundColor Cyan
      Write-Host "========================================" -ForegroundColor Cyan
      Write-Host ""
        
      # Configurar limpieza automática
      $cleanupAction = {
         Remove-TempResources
      }
      Register-EngineEvent -SourceIdentifier PowerShell.Exiting -Action $cleanupAction | Out-Null
        
      # Inicializar entorno
      Initialize-Environment
        
      # Verificar estado del sistema
      if (-not (Test-SystemHealth)) {
         Write-Log "Sistema no está saludable - abortando" "ERROR"
         return 1
      }
        
      # Variables para resultados
      $smokeResult = $true
      $backendResult = $true  
      $frontendResult = $true
        
      # Ejecutar smoke tests
      if (-not $SkipSmoke) {
         $smokeResult = Invoke-SmokeTests
         if (-not $smokeResult) {
            $overallResult = $false
         }
      }
        
      # Ejecutar pruebas de backend
      if (-not $FrontendOnly) {
         $backendResult = Invoke-BackendRegression
         if (-not $backendResult) {
            $overallResult = $false
         }
      }
        
      # Ejecutar pruebas de frontend  
      if (-not $BackendOnly) {
         $frontendResult = Invoke-FrontendRegression
         if (-not $frontendResult) {
            $overallResult = $false
         }
      }
        
      # Generar reporte de análisis
      $analysisResult = New-AnalysisReport -BackendResult $backendResult -FrontendResult $frontendResult -SmokeResult $smokeResult
        
      $endTime = Get-Date
      $duration = ($endTime - $startTime).TotalSeconds
        
      Write-Host ""
      if ($overallResult -and $analysisResult -eq "PASS") {
         Write-Log "🎉 REGRESIÓN COMPLETADA EXITOSAMENTE" "SUCCESS"
         Write-Log "Duración: $([math]::Round($duration, 1))s" "SUCCESS" 
         Write-Log "¡Sistema listo para deploy!" "SUCCESS"
         return 0
      }
      else {
         Write-Log "❌ REGRESIÓN FALLÓ" "ERROR"
         Write-Log "Duración: $([math]::Round($duration, 1))s" "ERROR"
         Write-Log "¡NO hacer deploy hasta resolver fallos!" "ERROR"
         return 1
      }
        
   }
   catch {
      Write-Log "Error inesperado: $($_.Exception.Message)" "ERROR"
      if ($Verbose) {
         Write-Log "Stack trace: $($_.Exception.StackTrace)" "ERROR"
      }
      return 1
   }
   finally {
      # Limpiar recursos
      Remove-TempResources
   }
}

# Ejecutar función principal
exit (Main)
