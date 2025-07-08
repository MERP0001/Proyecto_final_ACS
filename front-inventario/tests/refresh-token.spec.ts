import { test, expect } from '@playwright/test';
import { setupAuth } from './global.setup';
import { clearAuthState } from './utils/auth';

const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de Refresh Token', () => {
  test.beforeEach(async ({ page }) => {
    await clearAuthState(page);
  });

  test('Debe renovar el token automáticamente cuando expira', async ({ page, request }) => {
    // Obtener tokens iniciales
    const authData = await setupAuth(request);
    
    // Establecer tokens en localStorage
    await page.evaluate((auth) => {
      // Establecer un token expirado
      const expiredToken = auth.token.split('.').map((part: string, i: number) => {
        if (i === 1) {
          // Decodificar payload
          const payload = JSON.parse(atob(part));
          // Establecer exp a un tiempo pasado
          payload.exp = Math.floor(Date.now() / 1000) - 3600;
          // Codificar de nuevo
          return btoa(JSON.stringify(payload));
        }
        return part;
      }).join('.');

      localStorage.setItem('token', expiredToken);
      localStorage.setItem('refreshToken', auth.refreshToken);
    }, authData);

    // Navegar al dashboard
    await page.goto(`${baseURL}/dashboard`);
    
    // Verificar que seguimos en el dashboard (el token se renovó automáticamente)
    await expect(page).toHaveURL(`${baseURL}/dashboard`);
    
    // Verificar que el token fue renovado
    const newToken = await page.evaluate(() => localStorage.getItem('token'));
    expect(newToken).not.toBe(authData.token);
  });

  test('Debe redirigir al login si el refresh token es inválido', async ({ page }) => {
    // Establecer tokens inválidos
    await page.evaluate(() => {
      localStorage.setItem('token', 'invalid_token');
      localStorage.setItem('refreshToken', 'invalid_refresh_token');
    });

    // Intentar acceder al dashboard
    await page.goto(`${baseURL}/dashboard`);
    
    // Verificar redirección al login
    await expect(page).toHaveURL(`${baseURL}/login`);
  });

  test('Debe mantener múltiples peticiones en cola durante el refresh', async ({ page, request }) => {
    // Obtener tokens iniciales
    const authData = await setupAuth(request);
    
    // Establecer tokens en localStorage con token expirado
    await page.evaluate((auth) => {
      const expiredToken = auth.token.split('.').map((part: string, i: number) => {
        if (i === 1) {
          const payload = JSON.parse(atob(part));
          payload.exp = Math.floor(Date.now() / 1000) - 3600;
          return btoa(JSON.stringify(payload));
        }
        return part;
      }).join('.');

      localStorage.setItem('token', expiredToken);
      localStorage.setItem('refreshToken', auth.refreshToken);
    }, authData);

    // Navegar al dashboard
    await page.goto(`${baseURL}/dashboard`);

    // Realizar múltiples peticiones simultáneas
    const [response1, response2, response3] = await Promise.all([
      page.evaluate(() => fetch('/api/productos').then(r => r.ok)),
      page.evaluate(() => fetch('/api/productos/1').then(r => r.ok)),
      page.evaluate(() => fetch('/api/productos/2').then(r => r.ok))
    ]);

    // Verificar que todas las peticiones fueron exitosas
    expect(response1).toBeTruthy();
    expect(response2).toBeTruthy();
    expect(response3).toBeTruthy();
  });
}); 