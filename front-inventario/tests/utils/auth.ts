import { readFileSync } from 'fs';
import path from 'path';
import { Page } from '@playwright/test';

/**
 * Obtiene los tokens de autenticación guardados durante el setup
 */
export function getStoredAuth() {
  try {
    const authFile = path.join(process.cwd(), 'test-results', 'auth.json');
    const authData = JSON.parse(readFileSync(authFile, 'utf8'));
    return authData;
  } catch (error) {
    console.error('Error al leer los tokens de autenticación:', error);
    return null;
  }
}

/**
 * Limpia el estado de autenticación
 */
export async function clearAuthState(page: Page) {
  await page.route('**/*', async route => {
    await route.continue();
  });
  
  await page.context().clearCookies();
  
  // Usar context.addInitScript en lugar de evaluate
  await page.context().addInitScript(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
  });
}

/**
 * Establece el estado de autenticación
 */
export async function setAuthState(page: Page, token: string, refreshToken: string) {
  await page.context().addInitScript(data => {
    window.localStorage.setItem('token', data.token);
    window.localStorage.setItem('refreshToken', data.refreshToken);
  }, { token, refreshToken });
}

/**
 * Verifica si el usuario está autenticado
 */
export async function isAuthenticated(page: Page): Promise<boolean> {
  const token = await page.evaluate(() => window.localStorage.getItem('token'));
  return !!token;
} 