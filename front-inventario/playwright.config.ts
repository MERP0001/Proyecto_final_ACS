import { defineConfig, devices } from '@playwright/test';

/**
 * Configuración para las pruebas de Playwright
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  // Directorio con las pruebas
  testDir: './tests',
  
  // Patrón para los archivos de pruebas
  testMatch: '**/*.spec.ts',
  
  // Tiempo máximo para cada test en milisegundos
  timeout: 45000,
  
  // Número máximo de fallos permitidos (0 para ejecutar todos)
  maxFailures: 0,
  
  // Configuración del servidor web para pruebas
  webServer: {
    command: 'echo "Using existing server"',
    url: 'http://localhost:3000',
    reuseExistingServer: true,
    timeout: 120000, // 2 minutos para iniciar el servidor
  },
  
  // Configuración global para todas las pruebas
  use: {
    // Base URL para todas las pruebas
    baseURL: 'http://localhost:3000',
    
    // URL base para peticiones API
    extraHTTPHeaders: {
      'Accept': 'application/json',
    },

    // Configuración para peticiones API
    contextOptions: {
      baseURL: 'http://localhost:8080',
    },
    
    // Capturar captura de pantalla en caso de fallo
    screenshot: 'only-on-failure',
    
    // Grabar video en caso de fallo
    video: 'retain-on-failure',
    
    // Trazar las pruebas para mejorar la depuración
    trace: 'retain-on-failure',
    
    // Viewport base predeterminado
    viewport: { width: 1280, height: 720 },
    
    // Configuración de tiempos de espera
    actionTimeout: 30000,
    navigationTimeout: 45000,
    
    // Configuración adicional para estabilidad
    bypassCSP: true, // Bypass Content Security Policy
    ignoreHTTPSErrors: true,
  },
  
  // Directorio para los informes
  outputDir: 'test-results',
  
  // Navegadores y dispositivos a probar
  projects: [
    // Navegadores de escritorio
    {
      name: 'chromium',
      use: { 
        ...devices['Desktop Chrome'],
        launchOptions: {
          args: ['--disable-web-security']
        }
      },
    },
    {
      name: 'firefox',
      use: { 
        ...devices['Desktop Firefox'],
        launchOptions: {
          firefoxUserPrefs: {
            'security.fileuri.strict_origin_policy': false
          }
        }
      },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    
    // Dispositivos móviles
    {
      name: 'mobile-chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'mobile-safari',
      use: { ...devices['iPhone 12'] },
    },
    
    // Tablet
    {
      name: 'tablet',
      use: { ...devices['iPad Pro 11'] },
    },
  ],
  
  // Ejecutar las pruebas en paralelo
  fullyParallel: true,
  
  // Reintentar pruebas fallidas en CI
  retries: process.env.CI ? 2 : 1,
  
  // Reporteros para la salida
  reporter: [
    ['html', { open: 'never' }], // Reporte HTML
    ['list'], // Reporte en consola
    ['junit', { outputFile: 'test-results/junit.xml' }], // Reporte JUnit para CI
    ['json', { outputFile: 'test-results/test-results.json' }], // JSON para procesamiento
  ],
}); 