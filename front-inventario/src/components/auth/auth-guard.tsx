"use client";

import { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { Skeleton } from "../ui/skeleton";

const PUBLIC_PATHS = ['/login', '/register'];

interface AuthGuardProps {
    children: React.ReactNode;
}

export function AuthGuard({ children }: AuthGuardProps) {
    const { isAuthenticated, isLoading } = useAuth();
    const router = useRouter();
    const pathname = usePathname();
    const isPublicPath = PUBLIC_PATHS.includes(pathname);

    useEffect(() => {
        if (isLoading) {
            return;
        }

        // Si el usuario está autenticado y trata de acceder a una ruta pública,
        // lo redirigimos al dashboard.
        if (isAuthenticated && isPublicPath) {
            router.push("/dashboard");
            return;
        }

        // Si el usuario no está autenticado y la ruta no es pública,
        // lo redirigimos al login.
        if (!isAuthenticated && !isPublicPath) {
            router.push("/login");
            return;
        }

    }, [isAuthenticated, isLoading, router, pathname, isPublicPath]);

    // Mientras se carga la información de autenticación, mostramos un esqueleto.
    if (isLoading) {
        return (
            <div className="flex flex-col space-y-3 p-4">
                <Skeleton className="h-[125px] w-full rounded-xl" />
                <div className="space-y-2">
                    <Skeleton className="h-4 w-full" />
                    <Skeleton className="h-4 w-3/4" />
                </div>
            </div>
        );
    }

    // Si la ruta es pública o el usuario está autenticado, mostramos el contenido.
    if (isPublicPath || isAuthenticated) {
        return <>{children}</>;
    }
    
    // Si no, no mostramos nada mientras ocurre la redirección.
    return null;
} 