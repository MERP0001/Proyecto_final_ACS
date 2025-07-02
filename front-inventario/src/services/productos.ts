import api from '@/lib/api';
import { ProductoDTO, ProductoForm, ProductoFilters, PaginatedResponse } from '@/types';

export const productosService = {
  // Obtener todos los productos con paginación
  getProductos: async (page = 0, size = 10): Promise<PaginatedResponse<ProductoDTO>> => {
    const response = await api.get(`/productos?page=${page}&size=${size}`);
    return response.data;
  },

  // Buscar productos con filtros
  searchProductos: async (
    filters: ProductoFilters,
    page = 0,
    size = 10
  ): Promise<PaginatedResponse<ProductoDTO>> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(filters.nombre && { nombre: filters.nombre }),
      ...(filters.categoria && { categoria: filters.categoria }),
      ...(filters.precioMin && { precioMin: filters.precioMin.toString() }),
      ...(filters.precioMax && { precioMax: filters.precioMax.toString() }),
    });

    const response = await api.get(`/productos/buscar?${params}`);
    return response.data;
  },

  // Obtener producto por ID
  getProducto: async (id: number): Promise<ProductoDTO> => {
    const response = await api.get(`/productos/${id}`);
    return response.data;
  },

  // Crear nuevo producto
  createProducto: async (producto: ProductoForm): Promise<ProductoDTO> => {
    try {
      const response = await api.post('/productos', producto);
      return response.data;
    } catch (error) {
      console.error('Error al crear producto:', error);
      throw error;
    }
  },

  // Actualizar producto
  updateProducto: async (id: number, producto: ProductoForm): Promise<ProductoDTO> => {
    const response = await api.put(`/productos/${id}`, producto);
    return response.data;
  },

  // Actualizar stock
  updateStock: async (id: number, cantidad: number): Promise<ProductoDTO> => {
    const response = await api.patch(`/productos/${id}/stock?cantidad=${cantidad}`);
    return response.data;
  },

  // Eliminar producto
  deleteProducto: async (id: number): Promise<void> => {
    await api.delete(`/productos/${id}`);
  },

  // Obtener productos con stock bajo
  getProductosStockBajo: async (minimo = 10): Promise<ProductoDTO[]> => {
    const response = await api.get(`/productos/stock-bajo?minimo=${minimo}`);
    return response.data;
  },

  // Obtener categorías
  getCategorias: async (): Promise<string[]> => {
    const response = await api.get('/productos/categorias');
    return response.data;
  },

  // Calcular valor total del inventario
  getValorTotalInventario: async (): Promise<number> => {
    const response = await api.get('/productos/valor-total');
    return response.data;
  },
}; 