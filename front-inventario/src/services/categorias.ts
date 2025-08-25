import api from '@/lib/api';
import { Categoria, CategoriaForm, PaginatedResponse } from '@/types';

export const categoriasService = {
  // Obtener todas las categorías activas
  getCategorias: async (): Promise<Categoria[]> => {
    try {
      const response = await api.get('/api/categorias/activas');
      // Asegurar que siempre devolvemos un array
      const data = Array.isArray(response.data) ? response.data : [];
      return data;
    } catch (error) {
      console.error('Error al obtener categorías:', error);
      console.warn('Usando categorías fallback...');
      // Fallback a categorías básicas si falla la API
      return [
        { id: 1, nombre: "Electrónicos", descripcion: "Productos electrónicos", activo: true },
        { id: 2, nombre: "Laptops", descripcion: "Computadoras portátiles", activo: true },
        { id: 3, nombre: "Smartphones", descripcion: "Teléfonos móviles", activo: true },
        { id: 4, nombre: "Tablets", descripcion: "Tabletas", activo: true },
        { id: 5, nombre: "Accesorios", descripcion: "Accesorios diversos", activo: true },
        { id: 6, nombre: "Gaming", descripcion: "Productos para videojuegos", activo: true },
        { id: 7, nombre: "Audio", descripcion: "Equipos de audio", activo: true },
      ];
    }
  },

  // Obtener categorías con paginación
  getCategoriasConPaginacion: async (page = 0, size = 10): Promise<PaginatedResponse<Categoria>> => {
    try {
      const response = await api.get(`/api/categorias?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener categorías paginadas:', error);
      throw error;
    }
  },

  // Obtener categoría por ID
  getCategoria: async (id: number): Promise<Categoria> => {
    try {
      const response = await api.get(`/api/categorias/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener categoría:', error);
      throw error;
    }
  },

  // Crear nueva categoría
  createCategoria: async (categoria: CategoriaForm): Promise<Categoria> => {
    try {
      const response = await api.post('/api/categorias', categoria);
      return response.data;
    } catch (error) {
      console.error('Error al crear categoría:', error);
      throw error;
    }
  },

  // Actualizar categoría
  updateCategoria: async (id: number, categoria: CategoriaForm): Promise<Categoria> => {
    try {
      const response = await api.put(`/api/categorias/${id}`, categoria);
      return response.data;
    } catch (error) {
      console.error('Error al actualizar categoría:', error);
      throw error;
    }
  },

  // Eliminar categoría (soft delete)
  deleteCategoria: async (id: number): Promise<void> => {
    try {
      await api.delete(`/api/categorias/${id}`);
    } catch (error) {
      console.error('Error al eliminar categoría:', error);
      throw error;
    }
  },

  // Buscar categorías por nombre
  buscarCategorias: async (nombre: string): Promise<Categoria[]> => {
    try {
      const response = await api.get(`/api/categorias/buscar?nombre=${nombre}`);
      return response.data;
    } catch (error) {
      console.error('Error al buscar categorías:', error);
      throw error;
    }
  }
};
