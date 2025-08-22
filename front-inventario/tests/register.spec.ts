import { test, expect } from '@playwright/test';

/**
 * ANÁLISIS PREVIO AL TEST (v2):
 * 
 * ¿Comportamiento a garantizar?
 * Que el flujo de registro funciona de principio a fin, respetando la UI real.
 * 
 * ¿Condición validada?
 * El test debe usar los selectores EXACTOS que se renderizan en el DOM,
 * incluyendo la capitalización precisa de las etiquetas. Tras el envío,
 * debe aparecer la vista de "Registro Exitoso".
 */
test('Un nuevo usuario debería poder registrarse correctamente', async ({ page }) => {

  // 1. NAVEGAR a la página de registro.
  await page.goto('/register');

  // 2. VERIFICAR la carga de la página.
  await expect(page.getByRole('heading', { name: 'Crear una Cuenta' })).toBeVisible();

  // 3. ACTUAR rellenando el formulario con selectores precisos.
  const timestamp = Date.now();
  // Corregido: 'Nombre de usuario' con 'u' minúscula, como en el HTML.
  await page.getByLabel('Nombre de usuario').fill(`testuser_${timestamp}`);
  // Corregido: 'Email' con 'E' mayúscula.
  await page.getByLabel('Email').fill(`testuser_${timestamp}@example.com`);
  // Corregido: 'Nombre Completo' con mayúsculas.
  await page.getByLabel('Nombre Completo').fill('Usuario de Prueba');
  await page.getByLabel('Contraseña').fill('password123');
  
  await page.getByRole('button', { name: 'Crear Cuenta' }).click();

  // 4. VERIFICAR el resultado.
  await expect(page.getByRole('heading', { name: '¡Registro Exitoso!' })).toBeVisible();
}); 