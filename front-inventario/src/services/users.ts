import api from '@/lib/api';
import { PaginatedResponse, User, AuthRequest } from '@/types';

export const userService = {
  getAllUsers: async (): Promise<User[]> => {
    try {
      const response = await api.get<User[]>('/users');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener los usuarios:', error.response?.data || error.message);
      throw error;
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