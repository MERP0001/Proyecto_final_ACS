import { test, expect } from '@playwright/test';
import { clearAuthState } from './utils/auth';

const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de diseño responsivo', () => {
  test.beforeEach(async ({ page }) => {
    await clearAuthState(page);
  });

  test('La página de login debe adaptarse a dispositivos móviles', async ({ page }) => {
    // Configurar viewport móvil
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto(`${baseURL}/login`);

    // Esperar a que la página esté completamente cargada
    await expect(page.getByRole('heading', { name: 'Sistema de Inventarios' })).toBeVisible({ timeout: 20000 });
    await expect(page.getByRole('button', { name: 'Iniciar Sesión' })).toBeVisible({ timeout: 20000 });
    
    // Verificar que los campos están apilados verticalmente
    const form = page.locator('form');
    await expect(form).toBeVisible({ timeout: 20000 });
    await expect(form).toHaveCSS('flex-direction', 'column');

    // Capturar screenshot para verificación visual
    await page.screenshot({ path: './test-results/login-mobile.png' });
  });

  test('La página de login debe adaptarse a tablets', async ({ page }) => {
    // Configurar viewport tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto(`${baseURL}/login`);

    // Esperar a que la página esté completamente cargada
    await expect(page.getByRole('heading', { name: 'Sistema de Inventarios' })).toBeVisible({ timeout: 20000 });
    await expect(page.getByRole('button', { name: 'Iniciar Sesión' })).toBeVisible({ timeout: 20000 });

    // Verificar layout específico para tablet
    const loginCard = page.locator('.card').first();
    await expect(loginCard).toBeVisible({ timeout: 20000 });
    await expect(loginCard).toHaveCSS('max-width', '480px');

    // Capturar screenshot para verificación visual
    await page.screenshot({ path: './test-results/login-tablet.png' });
  });

  test('El dashboard debe ser responsivo', async ({ page }) => {
    // Login primero
    await page.goto(`${baseURL}/login`);
    await expect(page.getByRole('heading', { name: 'Sistema de Inventarios' })).toBeVisible({ timeout: 20000 });
    
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    
    const loginButton = page.getByRole('button', { name: 'Iniciar Sesión' });
    await loginButton.waitFor({ state: 'visible', timeout: 20000 });
    await loginButton.click();

    // Esperar redirección al dashboard y carga completa
    await expect(page).toHaveURL(`${baseURL}/dashboard`, { timeout: 20000 });
    await expect(page.getByText('Sistema de Gestión de Inventarios')).toBeVisible({ timeout: 20000 });

    // Prueba en escritorio
    await page.setViewportSize({ width: 1280, height: 720 });
    // Esperar a que el sidebar se cargue y sea visible
    await expect(page.locator('nav.sidebar')).toBeVisible({ timeout: 20000 });
    await page.waitForTimeout(1000); // Esperar a que las transiciones CSS se completen
    await expect(page.locator('nav.sidebar')).not.toHaveClass(/.*collapsed.*/, { timeout: 20000 });

    // Prueba en tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.waitForTimeout(1000); // Esperar a que las transiciones CSS se completen
    await expect(page.locator('nav.sidebar')).toBeVisible({ timeout: 20000 });
    await expect(page.locator('nav.sidebar')).toHaveClass(/.*collapsed.*/, { timeout: 20000 });

    // Prueba en móvil
    await page.setViewportSize({ width: 375, height: 667 });
    await page.waitForTimeout(1000); // Esperar a que las transiciones CSS se completen
    
    // En móvil, el sidebar debería estar oculto inicialmente
    await expect(page.locator('nav.sidebar')).not.toBeVisible({ timeout: 20000 });
    await expect(page.getByRole('button', { name: 'Menu' })).toBeVisible({ timeout: 20000 });

    // Probar menú móvil
    await page.getByRole('button', { name: 'Menu' }).click();
    await page.waitForTimeout(500); // Esperar a que la animación del menú se complete
    await expect(page.locator('nav.sidebar')).toBeVisible({ timeout: 20000 });
  });
}); 