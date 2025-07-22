import { test, expect } from '@playwright/test';

async function login(page: any) {
  await page.goto('/login');
  await page.getByLabel('Nombre de Usuario').fill('admin');
  await page.getByLabel('Contraseña').fill('admin123');
  await page.getByRole('button', { name: 'Iniciar Sesión' }).click();
  await page.waitForURL('**/dashboard');
}

/**
 * ANÁLISIS PREVIO AL TEST:
 * 
 * ¿Qué comportamiento específico se quiere garantizar aquí?
 * Se garantiza que un usuario admin puede completar el flujo de creación de un
 * nuevo producto, desde el formulario hasta su aparición en la tabla de inventario.
 * 
 * ¿Qué condición se está validando y por qué importa?
 * Se valida el flujo completo de escritura (Create). Desde la UI se envía una
 * petición a la API, que persiste los datos, y la UI se actualiza para reflejar
 * el nuevo estado. Es la prueba fundamental de la funcionalidad de negocio.
 * 
 * ¿Qué pasaría si este test no existiera?
 * Un bug podría bloquear la adición de nuevos productos al inventario,
 * lo que haría inútil la aplicación.
 */
test('Un usuario administrador debería poder crear un nuevo producto', async ({ page }) => {

  // 1. Hacer login
  await login(page);
  
  // 2. NAVEGAR a la página de la lista de productos.
  await page.goto('/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();

  // 3. ACTUAR: Iniciar el proceso de creación.
  await page.getByRole('button', { name: 'Agregar Producto' }).click();
  
  // 4. VERIFICAR que estamos en la página del formulario.
  await page.waitForURL('**/productos/nuevo');
  await expect(page.getByRole('heading', { name: 'Nuevo Producto' })).toBeVisible();

  // 5. ACTUAR: Rellenar el formulario con datos únicos.
  const nombreProducto = `Café de Origen Único ${Date.now()}`;
  await page.getByLabel('Nombre del Producto').fill(nombreProducto);
  await page.getByLabel('Descripción').fill('Un café excepcional de las montañas de Colombia.');
  
  await page.getByLabel('Categoría').click();
  await page.getByRole('option', { name: 'Gaming' }).click();

  await page.getByLabel('Precio').fill('15.99');
  await page.getByLabel('Cantidad Inicial').fill('100');
  
  // 6. ACTUAR: Enviar el formulario.
  await page.getByRole('button', { name: 'Guardar Producto' }).click();

  // 7. VERIFICAR el resultado final.
  await page.waitForURL('**/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
  await expect(page.getByRole('cell', { name: nombreProducto })).toBeVisible();
});

test('Un usuario debería poder editar un producto existente', async ({ page }) => {
  // 1. Hacer login
  await login(page);
  
  // 2. Ir a productos y seleccionar uno para editar
  await page.goto('/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
  await page.waitForSelector('tbody tr', { timeout: 10000 });
  
  // 3. Hacer clic en el dropdown del primer producto y seleccionar "Editar"
  await page.locator('tbody tr').first().locator('button[class*="h-8 w-8 p-0"]').click();
  await page.getByRole('menuitem').filter({ hasText: 'Editar' }).click();
  
  // 4. Verificar que estamos en la página de editar
  await page.waitForURL('**/productos/*/editar');
  await expect(page.getByRole('heading', { name: 'Editar Producto' })).toBeVisible();
  
  // 5. Actualizar campos del formulario
  const nombreActualizado = `Producto Editado ${Date.now()}`;
  await page.getByLabel('Nombre del Producto').clear();
  await page.getByLabel('Nombre del Producto').fill(nombreActualizado);
  await page.getByLabel('Precio').clear();
  await page.getByLabel('Precio').fill('25.99');
  
  // 6. Guardar cambios
  await page.getByRole('button', { name: 'Guardar Cambios' }).click();
  
  // 7. Verificar redirección y cambios guardados
  await page.waitForURL('**/productos');
  await expect(page.getByRole('cell', { name: nombreActualizado })).toBeVisible();
  await expect(page.getByText('$25.99')).toBeVisible();
});

test('Un usuario debería poder actualizar el stock de un producto', async ({ page }) => {
  // 1. Hacer login
  await login(page);
  
  // 2. Ir a productos
  await page.goto('/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
  
  // 3. Hacer clic en el dropdown del primer producto y seleccionar "Actualizar Stock"
  await page.locator('tbody tr').first().locator('button[class*="h-8 w-8 p-0"]').click();
  await page.getByRole('menuitem').filter({ hasText: 'Actualizar Stock' }).click();
  
  // 4. Verificar que estamos en la página de stock
  await page.waitForURL('**/productos/*/stock');
  await expect(page.getByRole('heading', { name: 'Actualizar Stock' })).toBeVisible();
  
  // 5. Verificar que se muestra la información del producto
  await expect(page.getByText('Información del Producto')).toBeVisible();
  await expect(page.getByText('Stock Actual')).toBeVisible();
  
  // 6. Agregar cantidad al stock
  await page.getByLabel('Cantidad a modificar').fill('10');
  await page.getByRole('button', { name: 'Actualizar Stock' }).click();
  
  // 7. Confirmar en el dialog
  await expect(page.getByText('Confirmar actualización de stock')).toBeVisible();
  await page.getByRole('button', { name: 'Confirmar' }).click();
  
  // 8. Verificar redirección
  await page.waitForURL('**/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
});

test('Un usuario debería poder eliminar un producto', async ({ page }) => {
  // 1. Hacer login
  await login(page);
  
  // 2. Crear producto temporal
  await page.goto('/productos/nuevo');
  const nombreTemporal = `Producto a Eliminar ${Date.now()}`;
  await page.getByLabel('Nombre del Producto').fill(nombreTemporal);
  await page.getByLabel('Descripción').fill('Producto temporal para test de eliminación');
  await page.getByLabel('Categoría').click();
  await page.getByRole('option', { name: 'Gaming' }).click();
  await page.getByLabel('Precio').fill('99.99');
  await page.getByLabel('Cantidad Inicial').fill('1');
  await page.getByRole('button', { name: 'Guardar Producto' }).click();
  
  // 3. Verificar que el producto se creó
  await page.waitForURL('**/productos');
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
  await page.waitForSelector('tbody tr', { timeout: 10000 });
  await expect(page.getByRole('cell', { name: nombreTemporal })).toBeVisible();
  
  // 4. Buscar el producto específico y eliminarlo
  const filaProducto = page.locator('tr', { hasText: nombreTemporal });
  await filaProducto.locator('button[class*="h-8 w-8 p-0"]').click();
  
  // 5. Simular confirmación del dialog de eliminación
  page.on('dialog', async dialog => {
    expect(dialog.message()).toContain('eliminar este producto');
    await dialog.accept();
  });
  
  await page.getByRole('menuitem').filter({ hasText: 'Eliminar' }).click();
  
  // 6. Verificar que el producto ya no aparece en la lista
  await expect(page.getByRole('cell', { name: nombreTemporal })).not.toBeVisible();
}); 