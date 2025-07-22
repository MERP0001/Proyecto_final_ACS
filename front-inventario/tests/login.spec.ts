import { test, expect } from '@playwright/test';

/**
 * ANÁLISIS PREVIO AL TEST:
 * 
 * ¿Qué comportamiento específico se quiere garantizar aquí?
 * Se quiere garantizar que un usuario con credenciales correctas (el 'admin')
 * puede completar el flujo de login y acceder a la página principal post-autenticación.
 * 
 * ¿Qué condición se está validando y por qué importa?
 * Se valida la transición de '/login' a '/dashboard' y la aparición del título 'Dashboard'.
 * Esto confirma que toda la cadena de autenticación (UI -> API -> DB -> JWT -> UI) funciona.
 * Es la prueba más fundamental del sistema.
 * 
 * ¿Qué pasaría si este test no existiera?
 * Un bug podría impedir que CUALQUIER usuario inicie sesión, dejando la aplicación
 * completamente inutilizable. Sería un fallo crítico no detectado.
 * 
 * ¿Cuál sería una falsa señal de éxito?
 * Redirigir a /dashboard pero no mostrar el contenido real, sino una página de carga
 * infinita o un error. Por eso es crucial verificar un elemento DENTRO del dashboard.
 */
test('Un usuario administrador debería poder iniciar sesión correctamente', async ({ page }) => {

  // 1. NAVEGAR a la página de login.
  await page.goto('/login');

  // 2. VERIFICAR que la página ha cargado y es la correcta.
  // Usamos el rol 'heading' porque es semánticamente el título y es único.
  await expect(page.getByRole('heading', { name: 'Iniciar Sesión' })).toBeVisible();

  // 3. ACTUAR sobre la página rellenando los datos y enviando el formulario.
  await page.getByLabel('Nombre de Usuario').fill('admin');
  await page.getByLabel('Contraseña').fill('admin123');
  await page.getByRole('button', { name: 'Iniciar Sesión' }).click();

  // 4. VERIFICAR el resultado de la acción.
  // La URL debe cambiar a '/dashboard' y el título principal debe ser visible.
  await page.waitForURL('**/dashboard');
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
}); 