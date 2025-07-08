import { test, expect } from '@playwright/test';
import { setupAuth } from './global.setup';
import { clearAuthState } from './utils/auth';

const baseURL = process.env.BASE_URL || 'http://localhost:3000';

test.describe('CRUD de Productos', () => {
  test.beforeEach(async ({ page, request }) => {
    // Limpiar estado y autenticar
    await clearAuthState(page);
    const authData = await setupAuth(request);
    await page.evaluate((auth) => {
      localStorage.setItem('token', auth.token);
      localStorage.setItem('refreshToken', auth.refreshToken);
    }, authData);
    
    // Navegar a la sección de productos
    await page.goto(`${baseURL}/dashboard/productos`);
  });

  test('Debe mostrar la lista de productos correctamente', async ({ page }) => {
    // Verificar elementos de la tabla
    await expect(page.getByRole('table')).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Descripción' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Precio' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Stock' })).toBeVisible();
  });

  test('Debe crear un nuevo producto', async ({ page }) => {
    // Datos del nuevo producto
    const nuevoProducto = {
      nombre: `Test Producto ${Date.now()}`,
      descripcion: 'Producto de prueba automatizada',
      precio: '99.99',
      stock: '50'
    };

    // Navegar al formulario de nuevo producto
    await page.getByRole('link', { name: 'Nuevo Producto' }).click();
    await expect(page).toHaveURL(`${baseURL}/dashboard/productos/nuevo`);

    // Llenar el formulario
    await page.getByLabel('Nombre').fill(nuevoProducto.nombre);
    await page.getByLabel('Descripción').fill(nuevoProducto.descripcion);
    await page.getByLabel('Precio').fill(nuevoProducto.precio);
    await page.getByLabel('Stock').fill(nuevoProducto.stock);

    // Guardar producto
    await page.getByRole('button', { name: 'Guardar' }).click();

    // Verificar redirección y mensaje de éxito
    await expect(page).toHaveURL(`${baseURL}/dashboard/productos`);
    await expect(page.getByRole('alert')).toContainText('Producto creado');

    // Verificar que el producto aparece en la lista
    await expect(page.getByRole('cell', { name: nuevoProducto.nombre })).toBeVisible();
  });

  test('Debe editar un producto existente', async ({ page }) => {
    // Crear producto para editar
    const productoOriginal = {
      nombre: `Test Producto Editar ${Date.now()}`,
      descripcion: 'Producto para editar',
      precio: '100.00',
      stock: '10'
    };

    // Crear producto vía API
    await page.evaluate(async (prod) => {
      const response = await fetch('/api/productos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(prod)
      });
      return response.json();
    }, productoOriginal);

    // Recargar página
    await page.reload();

    // Encontrar y hacer clic en el botón de editar
    const filaProducto = page.getByRole('row', { name: productoOriginal.nombre });
    await filaProducto.getByRole('button', { name: 'Editar' }).click();

    // Modificar datos
    const nuevoPrecio = '150.00';
    const nuevoStock = '20';
    await page.getByLabel('Precio').fill(nuevoPrecio);
    await page.getByLabel('Stock').fill(nuevoStock);

    // Guardar cambios
    await page.getByRole('button', { name: 'Guardar' }).click();

    // Verificar actualización
    await expect(page).toHaveURL(`${baseURL}/dashboard/productos`);
    await expect(page.getByRole('alert')).toContainText('Producto actualizado');
    
    // Verificar nuevos valores en la tabla
    const filaPrecio = page.getByRole('cell', { name: nuevoPrecio });
    const filaStock = page.getByRole('cell', { name: nuevoStock });
    await expect(filaPrecio).toBeVisible();
    await expect(filaStock).toBeVisible();
  });

  test('Debe eliminar un producto', async ({ page }) => {
    // Crear producto para eliminar
    const productoEliminar = {
      nombre: `Test Producto Eliminar ${Date.now()}`,
      descripcion: 'Producto para eliminar',
      precio: '50.00',
      stock: '5'
    };

    // Crear producto vía API
    await page.evaluate(async (prod) => {
      const response = await fetch('/api/productos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(prod)
      });
      return response.json();
    }, productoEliminar);

    // Recargar página
    await page.reload();

    // Encontrar y hacer clic en el botón de eliminar
    const filaProducto = page.getByRole('row', { name: productoEliminar.nombre });
    await filaProducto.getByRole('button', { name: 'Eliminar' }).click();

    // Confirmar eliminación en el diálogo
    await page.getByRole('button', { name: 'Confirmar' }).click();

    // Verificar eliminación
    await expect(page.getByRole('alert')).toContainText('Producto eliminado');
    await expect(page.getByRole('cell', { name: productoEliminar.nombre })).not.toBeVisible();
  });

  test('Debe validar campos requeridos al crear producto', async ({ page }) => {
    // Navegar al formulario de nuevo producto
    await page.getByRole('link', { name: 'Nuevo Producto' }).click();

    // Intentar guardar sin llenar campos
    await page.getByRole('button', { name: 'Guardar' }).click();

    // Verificar mensajes de error
    await expect(page.getByText('El nombre es requerido')).toBeVisible();
    await expect(page.getByText('La descripción es requerida')).toBeVisible();
    await expect(page.getByText('El precio es requerido')).toBeVisible();
    await expect(page.getByText('El stock es requerido')).toBeVisible();
  });

  test('Debe validar valores numéricos en precio y stock', async ({ page }) => {
    // Navegar al formulario de nuevo producto
    await page.getByRole('link', { name: 'Nuevo Producto' }).click();

    // Llenar campos con valores inválidos
    await page.getByLabel('Nombre').fill('Producto Test');
    await page.getByLabel('Descripción').fill('Descripción test');
    await page.getByLabel('Precio').fill('abc');
    await page.getByLabel('Stock').fill('-10');

    // Intentar guardar
    await page.getByRole('button', { name: 'Guardar' }).click();

    // Verificar mensajes de error
    await expect(page.getByText('El precio debe ser un número válido')).toBeVisible();
    await expect(page.getByText('El stock debe ser un número positivo')).toBeVisible();
  });

  test('Debe mostrar mensaje cuando no hay productos', async ({ page }) => {
    // Eliminar todos los productos vía API
    await page.evaluate(async () => {
      const response = await fetch('/api/productos', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const productos = await response.json();
      
      // Eliminar cada producto
      for (const producto of productos) {
        await fetch(`/api/productos/${producto.id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
      }
    });

    // Recargar página
    await page.reload();

    // Verificar mensaje de no hay productos
    await expect(page.getByText('No hay productos disponibles')).toBeVisible();
  });
}); 