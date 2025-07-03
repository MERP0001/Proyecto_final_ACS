import { test, expect } from '@playwright/test';

// URL base de la aplicación
const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de diseño responsivo', () => {
  test('La página de login debe adaptarse a dispositivos móviles', async ({ page }) => {
    // Establecer viewport de móvil
    await page.setViewportSize({ width: 375, height: 667 }); // iPhone SE
    
    // Navegar a la página de login
    await page.goto(`${baseURL}/login`);
    
    // Verificar elementos principales
    await expect(page.getByText('Sistema de Inventarios')).toBeVisible();
    await expect(page.getByText('Iniciar Sesión')).toBeVisible();
    
    // Capturar screenshot para verificación visual
    await page.screenshot({ path: './test-results/login-mobile.png' });
  });
  
  test('La página de login debe adaptarse a tablets', async ({ page }) => {
    // Establecer viewport de tablet
    await page.setViewportSize({ width: 768, height: 1024 }); // iPad
    
    // Navegar a la página de login
    await page.goto(`${baseURL}/login`);
    
    // Verificar elementos principales
    await expect(page.getByText('Sistema de Inventarios')).toBeVisible();
    await expect(page.getByText('Iniciar Sesión')).toBeVisible();
    
    // Capturar screenshot para verificación visual
    await page.screenshot({ path: './test-results/login-tablet.png' });
  });
  
  test('El dashboard debe ser responsivo', async ({ page }) => {
    // Login primero
    await page.goto(`${baseURL}/login`);
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Esperar redirección al dashboard
    await page.waitForURL(`${baseURL}/dashboard`, { timeout: 10000 });
    
    // Prueba en escritorio
    await page.setViewportSize({ width: 1280, height: 720 });
    await page.screenshot({ path: './test-results/dashboard-desktop.png' });
    
    // Prueba en tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.screenshot({ path: './test-results/dashboard-tablet.png' });
    
    // Prueba en móvil
    await page.setViewportSize({ width: 375, height: 667 });
    await page.screenshot({ path: './test-results/dashboard-mobile.png' });
    
    // En dispositivos móviles, verificar que el sidebar esté oculto inicialmente
    const sidebarVisible = await page.evaluate(() => {
      const sidebar = document.querySelector('[data-testid="sidebar"]');
      if (!sidebar) return false;
      
      // Obtener el estado de visibilidad usando computed style
      const style = window.getComputedStyle(sidebar);
      return style.display !== 'none' && style.visibility !== 'hidden';
    });
    
    expect(sidebarVisible).toBeFalsy();
  });
}); 