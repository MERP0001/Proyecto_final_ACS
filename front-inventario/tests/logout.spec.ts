import { test, expect } from '@playwright/test';

test('Un usuario debería poder cerrar sesión correctamente', async ({ page }) => {
  // 1. Hacer login primero
  await page.goto('/login');
  await page.getByLabel('Nombre de Usuario').fill('admin');
  await page.getByLabel('Contraseña').fill('admin123');
  await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
  
  // 2. Verificar que estamos en el dashboard
  await page.waitForURL('**/dashboard');
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();

  // 3. Hacer clic directamente en el botón "Cerrar Sesión"
  await page.getByRole('button', { name: 'Cerrar Sesión' }).click();
  
  // 4. Verificar que se redirige al login
  await page.waitForURL('**/login');
  await expect(page.getByRole('heading', { name: 'Iniciar Sesión' })).toBeVisible();
});
