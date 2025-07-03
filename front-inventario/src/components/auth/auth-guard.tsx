"use client";

import { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";

interface AuthGuardProps {
  children: React.ReactNode;
}

export function AuthGuard({ children }: AuthGuardProps) {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    // Si no está cargando y no está autenticado, redirigir al login
    if (!isLoading && !isAuthenticated && pathname !== "/login") {
      router.push("/login");
    }
    
    // Si está autenticado y está en la página de login, redirigir al dashboard
    if (!isLoading && isAuthenticated && pathname === "/login") {
      router.push("/dashboard");
    }
  }, [isAuthenticated, isLoading, router, pathname]);

  // Mostrar nada mientras está cargando o verificando autenticación
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  // Si no está autenticado y no está en la página de login, no mostrar nada
  if (!isAuthenticated && pathname !== "/login") {
    return null;
  }

  // Renderizar los children si está autenticado o está en la página de login
  return <>{children}</>;
} 