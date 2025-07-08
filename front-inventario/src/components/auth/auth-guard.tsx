"use client";

import { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";

// Definir rutas públicas y rutas por rol
const PUBLIC_PATHS = ['/login', '/register', '/forgot-password'];
const ROLE_ROUTES: Record<string, string[]> = {
    'ADMINISTRADOR': ['/dashboard', '/productos', '/usuarios'],
    'USUARIO': ['/dashboard', '/productos']
};

interface AuthGuardProps {
    children: React.ReactNode;
}

export function AuthGuard({ children }: AuthGuardProps) {
    const { isAuthenticated, isLoading, user } = useAuth();
    const router = useRouter();
    const pathname = usePathname();

    useEffect(() => {
        const checkAuth = async () => {
            // Si está cargando, no hacer nada aún
            if (isLoading) return;

            // Si es una ruta pública, permitir acceso
            if (PUBLIC_PATHS.includes(pathname)) {
                if (isAuthenticated) {
                    // Si está autenticado en una ruta pública, redirigir al dashboard
                    router.push('/dashboard');
                }
                return;
            }

            // Si no está autenticado y no es ruta pública, redirigir al login
            if (!isAuthenticated) {
                router.push('/login');
                return;
            }

            // Verificar permisos basados en rol
            if (user?.role) {
                const allowedRoutes = ROLE_ROUTES[user.role] || [];
                const hasAccess = allowedRoutes.some(route => pathname.startsWith(route));
                
                if (!hasAccess) {
                    console.warn(`Usuario con rol ${user.role} intentó acceder a ruta no autorizada: ${pathname}`);
                    router.push('/dashboard');
                }
            }
        };

        checkAuth();
    }, [isAuthenticated, isLoading, pathname, router, user?.role]);

    // Mostrar loading spinner mientras se verifica la autenticación
    if (isLoading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="flex flex-col items-center gap-4">
                    <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-sm text-gray-500">Verificando autenticación...</p>
                </div>
            </div>
        );
    }

    // Si es una ruta pública y no está autenticado, mostrar el contenido
    if (PUBLIC_PATHS.includes(pathname) && !isAuthenticated) {
        return <>{children}</>;
    }

    // Si está autenticado y tiene acceso a la ruta, mostrar el contenido
    if (isAuthenticated && user?.role) {
        const allowedRoutes = ROLE_ROUTES[user.role] || [];
        if (allowedRoutes.some(route => pathname.startsWith(route))) {
            return <>{children}</>;
        }
    }

    // Por defecto, no mostrar nada mientras se maneja la redirección
    return null;
} 