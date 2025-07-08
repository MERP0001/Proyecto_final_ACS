import { test, expect } from '@playwright/test';
import { clearAuthState } from './utils/auth';

// URL base de la aplicación
const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('Pruebas de autenticación', () => {
  test.beforeEach(async ({ page }) => {
    // Limpiar estado de autenticación y navegar a login
    await clearAuthState(page);
    await page.goto(`${baseURL}/login`);
    // Esperar a que la página esté completamente cargada
    await expect(page.getByRole('heading', { name: 'Sistema de Inventarios' })).toBeVisible({ timeout: 30000 });
  });

  test('Debe mostrar la página de login correctamente', async ({ page }) => {
    // Verificar que los elementos principales estén presentes
    await expect(page.getByText('Sistema de Inventarios')).toBeVisible();
    await expect(page.getByText('Iniciar Sesión')).toBeVisible();
    await expect(page.getByLabel('Nombre de Usuario')).toBeVisible();
    await expect(page.getByLabel('Contraseña')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Iniciar Sesión' })).toBeVisible();
  });

  test('Debe validar campos requeridos', async ({ page }) => {
    // Intentar enviar formulario vacío
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Verificar mensajes de error
    await expect(page.getByText('El nombre de usuario debe tener al menos 3 caracteres')).toBeVisible({ timeout: 10000 });
    await expect(page.getByText('La contraseña debe tener al menos 6 caracteres')).toBeVisible({ timeout: 10000 });
  });

  test('Debe validar longitud mínima de campos', async ({ page }) => {
    // Ingresar valores cortos
    await page.getByLabel('Nombre de Usuario').fill('ab');
    await page.getByLabel('Contraseña').fill('12345');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Verificar mensajes de error
    await expect(page.getByText('El nombre de usuario debe tener al menos 3 caracteres')).toBeVisible({ timeout: 10000 });
    await expect(page.getByText('La contraseña debe tener al menos 6 caracteres')).toBeVisible({ timeout: 10000 });
  });

  test('Debe mostrar error con credenciales inválidas', async ({ page }) => {
    // Ingresar credenciales inválidas
    await page.getByLabel('Nombre de Usuario').fill('usuario_invalido');
    await page.getByLabel('Contraseña').fill('clave_invalida');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Verificar mensaje de error
    await expect(page.getByRole('alert')).toBeVisible({ timeout: 20000 });
    await expect(page.getByRole('alert')).toContainText('Credenciales inválidas');
  });

  test('Debe autenticar correctamente con credenciales válidas', async ({ page }) => {
    // Ingresar credenciales válidas
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    
    // Esperar a que el botón sea clickeable
    const loginButton = page.getByRole('button', { name: 'Iniciar Sesión' });
    await loginButton.waitFor({ state: 'visible', timeout: 20000 });
    await loginButton.click();
    
    // Verificar redirección y elementos del dashboard con tiempo de espera extendido
    await expect(page).toHaveURL(`${baseURL}/dashboard`, { timeout: 20000 });
    await expect(page.getByText('Sistema de Gestión de Inventarios')).toBeVisible({ timeout: 20000 });
  });

  test('Debe mantener la sesión después de recargar', async ({ page }) => {
    // Login
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    
    // Esperar redirección con tiempo de espera extendido
    await expect(page).toHaveURL(`${baseURL}/dashboard`, { timeout: 20000 });
    
    // Esperar a que la página esté completamente cargada
    await expect(page.getByText('Sistema de Gestión de Inventarios')).toBeVisible({ timeout: 20000 });
    
    // Recargar y verificar que sigue autenticado
    await page.reload();
    await expect(page).toHaveURL(`${baseURL}/dashboard`, { timeout: 20000 });
    await expect(page.getByText('Sistema de Gestión de Inventarios')).toBeVisible({ timeout: 20000 });
  });
}); 