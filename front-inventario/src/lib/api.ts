import axios, { AxiosError } from 'axios';
import { authService } from '@/services/auth';

// Configuración base de la API
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: false
});

// Variable para controlar si estamos en proceso de refresh
let isRefreshing = false;
let failedQueue: Array<{
    resolve: (token: string) => void;
    reject: (error: any) => void;
}> = [];

const processQueue = (error: any | null, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token!);
        }
    });
    
    failedQueue = [];
};

// Interceptor para agregar el token JWT
api.interceptors.request.use(
    (config) => {
        const token = authService.getAccessToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor para manejar respuestas y renovación de tokens
api.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest: any = error.config;
        
        // Si no hay config o ya es un retry, rechazar
        if (!originalRequest || originalRequest._retry) {
            return Promise.reject(error);
        }

        // Si es error 401 y no es una ruta de auth
        if (error.response?.status === 401 && 
            !originalRequest.url?.includes('/auth/login') &&
            !originalRequest.url?.includes('/auth/refresh-token')) {
            
            if (isRefreshing) {
                try {
                    // Si ya está refrescando, esperar y reintentar con el nuevo token
                    const token = await new Promise<string>((resolve, reject) => {
                        failedQueue.push({ resolve, reject });
                    });
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    return api(originalRequest);
                } catch (err) {
                    return Promise.reject(err);
                }
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const response = await authService.refreshToken();
                const newToken = response.accessToken;
                
                // Procesar la cola de peticiones fallidas
                processQueue(null, newToken);
                
                // Actualizar el token en la petición original y reintentarla
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                // Si falla el refresh, procesar la cola con error y redirigir al login
                processQueue(refreshError, null);
                authService.logout();
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        // Si es error 403, limpiar auth y redirigir al login
        if (error.response?.status === 403) {
            authService.logout();
        }

        return Promise.reject(error);
    }
);

export default api; 