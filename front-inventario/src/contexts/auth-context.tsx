"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { authService } from "@/services/auth";
import { AuthRequest, AuthResponse } from "@/types";
import { useRouter } from "next/navigation";

interface AuthContextType {
    user: Partial<AuthResponse> | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (credentials: AuthRequest) => Promise<void>;
    logout: () => Promise<void>;
    error: string | null;
    clearError: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<Partial<AuthResponse> | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();

    useEffect(() => {
        const initAuth = async () => {
            try {
                if (authService.isAuthenticated()) {
                    const currentUser = authService.getCurrentUser();
                    if (currentUser) {
                        // Validar el token con el backend
                        const isValid = await authService.validateToken();
                        if (isValid) {
                            setUser(currentUser);
                        } else {
                            // Si el token no es válido y no se puede refrescar, hacer logout
                            await logout();
                        }
                    }
                }
            } catch (error) {
                console.error("Error al inicializar autenticación:", error);
            } finally {
                setIsLoading(false);
            }
        };

        initAuth();
    }, []);

    const login = async (credentials: AuthRequest) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await authService.login(credentials);
            setUser({
                username: response.username,
                email: response.email,
                nombreCompleto: response.nombreCompleto,
                role: response.role
            });
            router.push("/dashboard");
        } catch (error: any) {
            const errorMessage = error.response?.data?.message || 
                               "Error al iniciar sesión. Por favor, verifica tus credenciales.";
            setError(errorMessage);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const logout = async () => {
        setIsLoading(true);
        try {
            await authService.logout();
            setUser(null);
            router.push("/login");
        } catch (error) {
            console.error("Error durante el logout:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const clearError = () => setError(null);

    const value = {
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        error,
        clearError
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    
    if (context === undefined) {
        throw new Error("useAuth debe ser usado dentro de un AuthProvider");
    }
    
    return context;
}; 