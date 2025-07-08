import api from '@/lib/api';
import { AuthRequest, AuthResponse, RefreshTokenRequest } from '@/types';
import { jwtDecode } from 'jwt-decode';

const TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const USER_KEY = 'user';

export const authService = {
    login: async (credentials: AuthRequest): Promise<AuthResponse> => {
        try {
            const response = await api.post<AuthResponse>('/api/auth/login', credentials);
            const authData = response.data;
            
            // Guardar tokens y datos del usuario
            localStorage.setItem(TOKEN_KEY, authData.accessToken);
            localStorage.setItem(REFRESH_TOKEN_KEY, authData.refreshToken);
            localStorage.setItem(USER_KEY, JSON.stringify({
                username: authData.username,
                email: authData.email,
                nombreCompleto: authData.nombreCompleto,
                role: authData.role,
                expiresAt: authData.expiresAt
            }));
            
            return authData;
        } catch (error: any) {
            console.error('Error durante el login:', error.response?.data || error.message);
            throw error;
        }
    },

    refreshToken: async (): Promise<AuthResponse> => {
        try {
            const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
            if (!refreshToken) {
                throw new Error('No hay refresh token disponible');
            }

            const response = await api.post<AuthResponse>('/api/auth/refresh-token', {
                refreshToken
            } as RefreshTokenRequest);

            const authData = response.data;
            
            // Actualizar tokens
            localStorage.setItem(TOKEN_KEY, authData.accessToken);
            localStorage.setItem(REFRESH_TOKEN_KEY, authData.refreshToken);
            localStorage.setItem(USER_KEY, JSON.stringify({
                username: authData.username,
                email: authData.email,
                nombreCompleto: authData.nombreCompleto,
                role: authData.role,
                expiresAt: authData.expiresAt
            }));

            return authData;
        } catch (error) {
            console.error('Error al refrescar el token:', error);
            authService.logout();
            throw error;
        }
    },

    validateToken: async (): Promise<boolean> => {
        try {
            const token = localStorage.getItem(TOKEN_KEY);
            if (!token) return false;

            const response = await api.post<AuthResponse>('/api/auth/validate-token');
            return true;
        } catch (error) {
            console.error('Error al validar el token:', error);
            return false;
        }
    },

    logout: async () => {
        try {
            await api.post('/api/auth/logout');
        } catch (error) {
            console.error('Error durante el logout:', error);
        } finally {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(REFRESH_TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
        }
    },

    isAuthenticated: (): boolean => {
        const token = localStorage.getItem(TOKEN_KEY);
        if (!token) return false;
        
        try {
            const decoded: any = jwtDecode(token);
            const currentTime = Date.now() / 1000;
            
            // Si el token está próximo a expirar (menos de 5 minutos), intentar renovarlo
            if (decoded.exp - currentTime < 300) {
                authService.refreshToken().catch(() => {
                    // Si falla la renovación, se manejará en el interceptor
                    console.log('No se pudo renovar el token automáticamente');
                });
            }
            
            return decoded.exp > currentTime;
        } catch (error) {
            console.error('Error al decodificar token:', error);
            return false;
        }
    },

    getAccessToken: (): string | null => {
        return localStorage.getItem(TOKEN_KEY);
    },

    getRefreshToken: (): string | null => {
        return localStorage.getItem(REFRESH_TOKEN_KEY);
    },

    getCurrentUser: (): Partial<AuthResponse> | null => {
        const userStr = localStorage.getItem(USER_KEY);
        return userStr ? JSON.parse(userStr) : null;
    }
};