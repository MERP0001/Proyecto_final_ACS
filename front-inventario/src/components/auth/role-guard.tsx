"use client";

import { useAuth } from "@/contexts/auth-context";
import { useRouter, usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { Skeleton } from "../ui/skeleton";
import { Card, CardHeader, CardTitle, CardContent } from "../ui/card";
import { Button } from "../ui/button";

interface RoleGuardProps {
  allowedRoles: string[];
  children: React.ReactNode;
}

export function RoleGuard({ allowedRoles, children }: RoleGuardProps) {
  const { user, isAuthenticated, isLoading } = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    if (isLoading) {
      return;
    }

    if (!isAuthenticated) {
      router.push("/login");
      return;
    }

    const userRole = user?.role;
    if (userRole && allowedRoles.includes(userRole)) {
      setIsAuthorized(true);
    } else {
      // Si el rol no está permitido, redirigir a una página principal o de acceso denegado
      const fallbackPath = "/dashboard";
      if (pathname !== fallbackPath) {
        console.warn(`Acceso denegado para el rol: ${userRole}. Roles permitidos: ${allowedRoles.join(", ")}. Redirigiendo a ${fallbackPath}`);
        router.push(fallbackPath);
      } else {
        // Si ya estamos en la página de fallback y no tenemos acceso, evitamos el bucle.
        // Aquí podríamos mostrar un mensaje de "Acceso Denegado" en lugar de un loader infinito.
        setIsAuthorized(false);
      }
    }
  }, [user, isAuthenticated, isLoading, router, allowedRoles, pathname]);

  if (isLoading || !isAuthorized) {
    // Muestra un esqueleto o componente de carga mientras se verifica la autorización
    // Si no está autorizado, el usuario verá esto en lugar de la página.
    return (
      <div className="flex flex-col items-center justify-center h-full space-y-4 p-4">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-2xl text-center">Acceso Denegado</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-muted-foreground">
              No tienes los permisos necesarios para ver esta página.
            </p>
            <Button onClick={() => router.push('/login')} className="w-full mt-4">
              Cambiar de cuenta
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return <>{children}</>;
} 