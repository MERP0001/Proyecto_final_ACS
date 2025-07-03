import { test, expect } from '@playwright/test';

// URL base de la aplicación
const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de autenticación', () => {
  test.beforeEach(async ({ page }) => {
    // Navegar a la página de login antes de cada prueba
    await page.goto(`${baseURL}/login`);
  });

  test('Debe mostrar la página de login correctamente', async ({ page }) => {
    // Verificar que los elementos principales estén presentes
    await expect(page.getByText('Sistema de Inventarios')).toBeVisible();
    await expect(page.getByText('Iniciar Sesión')).toBeVisible();
    await expect(page.getByLabel('Nombre de Usuario')).toBeVisible();
    await expect(page.getByLabel('Contraseña')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Iniciar Sesión' })).toBeVisible();

    // Tomar captura de pantalla para verificación visual
    const viewport = page.viewportSize();
    const width = viewport ? viewport.width : 1280;
    const height = viewport ? viewport.height : 720;
    await page.screenshot({ path: `./test-results/login-page-${width}x${height}.png` });
  });

  test('Debe mostrar error con credenciales inválidas', async ({ page }) => {
    // Ingresar credenciales inválidas
    await page.getByLabel('Nombre de Usuario').fill('usuario_invalido');
    await page.getByLabel('Contraseña').fill('clave_invalida');
    
    // Enviar formulario
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Esperar a que aparezca el mensaje de error
    await expect(page.locator('.bg-red-50')).toBeVisible({ timeout: 5000 });
    
    // Verificar texto de error
    await expect(page.locator('.bg-red-50')).toContainText(/credenciales|inválidas|error/i);
  });

  test('Debe autenticar correctamente con credenciales válidas', async ({ page }) => {
    // Ingresar credenciales válidas (usar credenciales de prueba)
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    
    // Enviar formulario
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Esperar redirección al dashboard (timeout mayor por posible latencia)
    await page.waitForURL(`${baseURL}/dashboard`, { timeout: 10000 });
    
    // Verificar que estamos en el dashboard
    await expect(page).toHaveURL(`${baseURL}/dashboard`);
    
    // Verificar elementos del dashboard
    await expect(page.getByText('Sistema de Gestión de Inventarios')).toBeVisible();
  });

  test('Debe mantener la sesión después de recargar la página', async ({ page }) => {
    // Login primero
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Esperar redirección al dashboard
    await page.waitForURL(`${baseURL}/dashboard`, { timeout: 10000 });
    
    // Recargar la página
    await page.reload();
    
    // Verificar que seguimos en el dashboard (no redirige a login)
    await expect(page).toHaveURL(`${baseURL}/dashboard`);
  });
}); 