import { test, expect } from '@playwright/test';
import { setupAuth } from './global.setup';
import { clearAuthState } from './utils/auth';

const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de Logout', () => {
  test.beforeEach(async ({ page, request }) => {
    // Limpiar estado previo
    await clearAuthState(page);
    
    // Autenticar usuario
    const authData = await setupAuth(request);
    await page.evaluate((auth) => {
      localStorage.setItem('token', auth.token);
      localStorage.setItem('refreshToken', auth.refreshToken);
    }, authData);
    
    // Navegar al dashboard
    await page.goto(`${baseURL}/dashboard`);
  });

  test('Debe cerrar sesión correctamente desde el menú', async ({ page }) => {
    // Abrir menú de usuario
    await page.getByRole('button', { name: /perfil/i }).click();
    
    // Hacer clic en cerrar sesión
    await page.getByRole('menuitem', { name: /cerrar sesión/i }).click();
    
    // Verificar redirección al login
    await expect(page).toHaveURL(`${baseURL}/login`);
    
    // Verificar que los tokens fueron eliminados
    const token = await page.evaluate(() => localStorage.getItem('token'));
    const refreshToken = await page.evaluate(() => localStorage.getItem('refreshToken'));
    expect(token).toBeNull();
    expect(refreshToken).toBeNull();
    
    // Intentar acceder al dashboard debe redirigir al login
    await page.goto(`${baseURL}/dashboard`);
    await expect(page).toHaveURL(`${baseURL}/login`);
  });

  test('Debe cerrar sesión en todas las pestañas', async ({ browser }) => {
    // Crear dos contextos para simular dos pestañas
    const context1 = await browser.newContext();
    const context2 = await browser.newContext();
    const page1 = await context1.newPage();
    const page2 = await context2.newPage();

    // Autenticar en ambas pestañas
    for (const page of [page1, page2]) {
      await page.goto(`${baseURL}/login`);
      await page.getByLabel('Nombre de Usuario').fill('admin');
      await page.getByLabel('Contraseña').fill('admin123');
      await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
      await expect(page).toHaveURL(`${baseURL}/dashboard`);
    }

    // Cerrar sesión en la primera pestaña
    await page1.getByRole('button', { name: /perfil/i }).click();
    await page1.getByRole('menuitem', { name: /cerrar sesión/i }).click();

    // Verificar que la primera pestaña fue redirigida al login
    await expect(page1).toHaveURL(`${baseURL}/login`);

    // Recargar la segunda pestaña y verificar que también fue desconectada
    await page2.reload();
    await expect(page2).toHaveURL(`${baseURL}/login`);

    // Limpiar
    await context1.close();
    await context2.close();
  });

  test('Debe limpiar datos locales al cerrar sesión', async ({ page }) => {
    // Agregar algunos datos de prueba al localStorage
    await page.evaluate(() => {
      localStorage.setItem('userPreferences', JSON.stringify({ theme: 'dark' }));
      localStorage.setItem('lastVisitedPage', '/dashboard/productos');
    });

    // Cerrar sesión
    await page.getByRole('button', { name: /perfil/i }).click();
    await page.getByRole('menuitem', { name: /cerrar sesión/i }).click();

    // Verificar que los datos fueron limpiados
    const userPreferences = await page.evaluate(() => localStorage.getItem('userPreferences'));
    const lastVisitedPage = await page.evaluate(() => localStorage.getItem('lastVisitedPage'));
    expect(userPreferences).toBeNull();
    expect(lastVisitedPage).toBeNull();
  });

  test('Debe cancelar peticiones pendientes al cerrar sesión', async ({ page }) => {
    // Iniciar una petición larga
    const responsePromise = page.evaluate(() => 
      fetch('/api/productos/large-query').then(r => r.ok)
    );

    // Cerrar sesión inmediatamente
    await page.getByRole('button', { name: /perfil/i }).click();
    await page.getByRole('menuitem', { name: /cerrar sesión/i }).click();

    // Verificar que la petición fue cancelada (debe fallar)
    const response = await responsePromise;
    expect(response).toBeFalsy();
  });
}); 