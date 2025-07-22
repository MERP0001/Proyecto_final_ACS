import api from '@/lib/api';
import { PaginatedResponse, User, AuthRequest } from '@/types';

export const userService = {
  // Método original sin paginación (para compatibilidad)
  getAllUsers: async (): Promise<User[]> => {
    try {
      const response = await api.get<User[]>('/users');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener los usuarios:', error.response?.data || error.message);
      throw error;
    }
  },

  // Nuevo método con paginación
  getUsers: async (page = 0, size = 10): Promise<PaginatedResponse<User>> => {
    try {
      const response = await api.get<PaginatedResponse<User>>(`/users?page=${page}&size=${size}`);
      return response.data;
    } catch (error: any) {
      // Si el backend no soporta paginación todavía, simular paginación en el frontend
      console.warn('El endpoint no soporta paginación, usando fallback');
      const allUsers = await userService.getAllUsers();
      
      const startIndex = page * size;
      const endIndex = startIndex + size;
      const paginatedUsers = allUsers.slice(startIndex, endIndex);
      
      return {
        content: paginatedUsers,
        totalElements: allUsers.length,
        totalPages: Math.ceil(allUsers.length / size),
        size: size,
        number: page,
        first: page === 0,
        last: endIndex >= allUsers.length,
      };
    }
  },

  // Buscar usuarios con filtros y paginación
  searchUsers: async (
    searchTerm: string,
    page = 0,
    size = 10
  ): Promise<PaginatedResponse<User>> => {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        search: searchTerm,
      });

      const response = await api.get<PaginatedResponse<User>>(`/users/search?${params}`);
      return response.data;
    } catch (error: any) {
      // Fallback: buscar en todos los usuarios en el frontend
      console.warn('El endpoint de búsqueda no existe, usando fallback');
      const allUsers = await userService.getAllUsers();
      
      const filteredUsers = allUsers.filter(user =>
        user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.nombreCompleto.toLowerCase().includes(searchTerm.toLowerCase())
      );
      
      const startIndex = page * size;
      const endIndex = startIndex + size;
      const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
      
      return {
        content: paginatedUsers,
        totalElements: filteredUsers.length,
        totalPages: Math.ceil(filteredUsers.length / size),
        size: size,
        number: page,
        first: page === 0,
        last: endIndex >= filteredUsers.length,
      };
    }
  },

  getUserById: async (id: number): Promise<User> => {
    try {
      const response = await api.get<User>(`/users/${id}`);
      return response.data;
    } catch (error: any) {
      console.error(`Error al obtener el usuario con ID ${id}:`, error.response?.data || error.message);
      throw error;
    }
  },

  createUser: async (userData: AuthRequest): Promise<User> => {
    try {
      // Usar el endpoint de registro para la creación de usuarios desde el panel de admin
      const response = await api.post<User>('/auth/register', userData);
      return response.data;
    } catch (error: any) {
      console.error('Error al crear el usuario:', error.response?.data || error.message);
      throw error;
    }
  },

  updateUser: async (id: number, userData: Partial<User>): Promise<User> => {
    try {
      const response = await api.put<User>(`/users/${id}`, userData);
      return response.data;
    } catch (error: any) {
      console.error(`Error al actualizar el usuario con ID ${id}:`, error.response?.data || error.message);
      throw error;
    }
  },

  deleteUser: async (id: number): Promise<void> => {
    try {
      await api.delete(`/users/${id}`);
    } catch (error: any) {
      console.error(`Error al eliminar el usuario con ID ${id}:`, error.response?.data || error.message);
      throw error;
    }
  },
}; 