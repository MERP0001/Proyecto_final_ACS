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
  timeout: 30000,
  
  // Número máximo de fallos permitidos (0 para ejecutar todos)
  maxFailures: 0,
  
  // Capturar una captura de pantalla en cada fallo
  use: {
    // Capturar captura de pantalla en caso de fallo
    screenshot: 'only-on-failure',
    
    // Grabar video en caso de fallo
    video: 'on-first-retry',
    
    // Trazar las pruebas para mejorar la depuración
    trace: 'retain-on-failure',
    
    // Viewport base predeterminado
    viewport: { width: 1280, height: 720 },
  },
  
  // Directorio para los informes
  outputDir: 'test-results',
  
  // Navegadores y dispositivos a probar
  projects: [
    // Navegadores de escritorio
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
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
  
  // No reintentar pruebas fallidas
  retries: 0,
  
  // Reporteros para la salida
  reporter: [
    ['html', { open: 'never' }], // Reporte HTML
    ['list'], // Reporte en consola
  ],
}); 