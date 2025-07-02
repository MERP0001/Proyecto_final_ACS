// Tipos basados en las entidades del backend Spring Boot

export interface User {
  id: number;
  username: string;
  email: string;
  nombreCompleto: string;
  role: 'ADMINISTRADOR';
  activo: boolean;
  fechaCreacion: string;
  ultimoAcceso?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  username: string;
  email?: string;
  nombreCompleto?: string;
  role?: string;
  expiresAt?: string;
}

export interface ProductoDTO {
  id?: number;
  nombre: string;
  descripcion?: string;
  categoria: string;
  precio: number;
  cantidadInicial: number;
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