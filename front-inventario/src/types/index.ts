// Tipos basados en las entidades del backend Spring Boot

export interface User {
    id: number;
    username: string;
    email: string;
    nombreCompleto: string;
    role: 'ADMINISTRADOR' | 'USUARIO';
    activo: boolean;
    fechaCreacion?: string;
    ultimoAcceso?: string;
}

export interface AuthRequest {
    username: string;
    password?: string; // Hacer opcional para el formulario de admin
    email?: string;
    nombreCompleto?: string;
    role?: 'ADMINISTRADOR' | 'USUARIO';
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresAt: string;
    username: string;
    email: string;
    nombreCompleto: string;
    role: string;
    message?: string;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface ProductoDTO {
    id?: number;
    nombre: string;
    descripcion?: string;
    categoria: string;
    precio: number;
    cantidadInicial: number;
    cantidadActual: number;
    unidadMedida: string;
    activo?: boolean;
    fechaCreacion?: string;
    fechaActualizacion?: string;
}

export interface ProductoForm {
    nombre: string;
    descripcion: string;
    categoria: string;
    precio: number;
    cantidadInicial: number;
    unidadMedida: string;
}

export interface ProductoFilters {
    nombre?: string;
    categoria?: string;
    precioMin?: number;
    precioMax?: number;
}

export interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}

export interface ErrorResponse {
    message: string;
    status: number;
    timestamp: string;
    path: string;
} 