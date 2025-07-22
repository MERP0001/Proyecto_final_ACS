import { chromium, expect, type FullConfig } from '@playwright/test';

/**
 * ANÁLISIS:
 * Este script actúa como un "hook" global que se ejecuta UNA VEZ antes de toda la suite de pruebas.
 * Su único propósito es autenticar a un usuario (en este caso, 'admin') y guardar
 * su estado de sesión (cookies, localStorage) en un archivo.
 * Las pruebas posteriores usarán este archivo para iniciar sus ejecuciones ya autenticadas,
 * lo que las hace más rápidas y las desacopla de la lógica de login.
 * Es una dependencia crítica para todas las pruebas de funcionalidades protegidas.
 */
async function globalSetup(config: FullConfig) {
  // Extraemos la URL base y la ruta del archivo de estado desde la configuración.
  const { baseURL, storageState } = config.projects[0].use;
  if (!baseURL || !storageState) {
    throw new Error('baseURL y storageState deben estar definidos en playwright.config.ts');
  }

  // Lanzamos un navegador temporal para realizar el login.
  const browser = await chromium.launch();
  const page = await browser.newPage();

  try {
    // PASO PREVIO DE LIMPIEZA:
    // Navegamos a la raíz y forzamos un logout para limpiar cualquier sesión persistente.
    await page.goto(baseURL);
    // Usamos page.evaluate para llamar a la función de logout directamente desde el contexto de la app.
    // Es una forma robusta de limpiar el estado sin depender de interacciones de UI.
    await page.evaluate(() => window.localStorage.clear());

    // Ahora, procedemos con el flujo de login normal desde un estado garantizado como anónimo.
    await page.goto(baseURL + '/login');
    await expect(page.getByRole('heading', { name: 'Iniciar Sesión' })).toBeVisible();
    await page.getByLabel('Nombre de Usuario').fill('admin');
    await page.getByLabel('Contraseña').fill('admin123');
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
    await page.waitForURL('**/dashboard');
    await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();

    // El paso crucial: guardamos el estado de la sesión.
    await page.context().storageState({ path: storageState as string });
    console.log(`Estado de autenticación de admin guardado en ${storageState}`);

  } catch (error) {
    console.error('Fallo crítico en el global setup de autenticación:', error);
    throw error; // Si el setup falla, no se debe continuar con las pruebas.
  } finally {
    await browser.close();
  }
}

export default globalSetup; 