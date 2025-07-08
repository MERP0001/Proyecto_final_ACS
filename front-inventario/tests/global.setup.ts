import { test as setup, expect } from '@playwright/test';
import { writeFileSync, mkdirSync } from 'fs';
import path from 'path';
import type { APIRequestContext } from '@playwright/test';

const API_URL = 'http://localhost:8080';

// Función para realizar el login y guardar los tokens
async function setupAuth(request: APIRequestContext) {
  const loginResponse = await request.post(`${API_URL}/api/auth/login`, {
    data: {
      username: 'admin',
      password: 'admin123'
    },
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  });

  expect(loginResponse.ok()).toBeTruthy();
  
  const responseBody = await loginResponse.json();
  expect(responseBody.token).toBeDefined();
  expect(responseBody.refreshToken).toBeDefined();

  return {
    token: responseBody.token,
    refreshToken: responseBody.refreshToken
  };
}

// Setup global que se ejecuta una vez antes de todas las pruebas
setup('global setup', async ({ request }) => {
  try {
    // Realizar login y obtener tokens
    const authData = await setupAuth(request);

    // Crear directorio si no existe
    const testResultsDir = path.join(process.cwd(), 'test-results');
    mkdirSync(testResultsDir, { recursive: true });

    // Guardar tokens en un archivo para que las pruebas puedan acceder a ellos
    writeFileSync(
      path.join(testResultsDir, 'auth.json'),
      JSON.stringify(authData, null, 2)
    );
  } catch (error) {
    console.error('Error en el setup global:', error);
    throw error;
  }
});

// Exportar la función de setup para poder usarla en los tests
export { setupAuth }; 